package com.sentinel.data.repository

import com.sentinel.data.local.dao.ButtonTriggerDao
import com.sentinel.data.mapper.EntityMappers.toDomain
import com.sentinel.data.mapper.EntityMappers.toEntity
import com.sentinel.domain.model.ButtonTrigger
import com.sentinel.security.CryptoManager
import com.sentinel.security.SecurePreferences
import com.sentinel.util.Constants
import com.sentinel.util.JsonUtil
import com.sentinel.util.PasswordNormalizer
import com.sentinel.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ButtonTriggerRepository @Inject constructor(
    private val buttonTriggerDao: ButtonTriggerDao
) {
    fun observeAll(): Flow<List<ButtonTrigger>> =
        buttonTriggerDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeEnabled(): Flow<List<ButtonTrigger>> =
        buttonTriggerDao.observeEnabled().map { list -> list.map { it.toDomain() } }

    suspend fun save(trigger: ButtonTrigger): Long {
        val entity = trigger.toEntity()
        return if (entity.id == 0L) buttonTriggerDao.insert(entity) else {
            buttonTriggerDao.update(entity)
            entity.id
        }
    }
}

@Singleton
class PasswordRepository @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val cryptoManager: CryptoManager
) {
    fun getPasswordConfig(): PasswordConfig = PasswordConfig(
        mainPasswordHash = securePreferences.getString(Constants.KEY_MAIN_PASSWORD_HASH),
        duressPassword1Hash = securePreferences.getString(Constants.KEY_DURESS_PASSWORD_1_HASH),
        duressPassword2Hash = securePreferences.getString(Constants.KEY_DURESS_PASSWORD_2_HASH),
        duressPassword3Hash = securePreferences.getString(Constants.KEY_DURESS_PASSWORD_3_HASH),
        duress1Actions = loadActions("duress1_actions") ?: listOf(SecurityAction(ActionType.ACTIVATE_DECOY_MODE)),
        duress2Actions = loadActions("duress2_actions") ?: listOf(SecurityAction(ActionType.LOCK_APPLICATIONS)),
        duress3Actions = loadActions("duress3_actions") ?: listOf(
            SecurityAction(ActionType.EXECUTE_EMERGENCY_ACTIONS)
        ),
        biometricEnabled = securePreferences.getBoolean("biometric_enabled")
    )

    fun setMainPassword(password: String) {
        val normalized = PasswordNormalizer.normalize(password)
        securePreferences.putStringSync(
            Constants.KEY_MAIN_PASSWORD_HASH,
            cryptoManager.hashPassword(normalized)
        )
    }

    fun setDuressPassword(index: Int, password: String) {
        val key = when (index) {
            1 -> Constants.KEY_DURESS_PASSWORD_1_HASH
            2 -> Constants.KEY_DURESS_PASSWORD_2_HASH
            3 -> Constants.KEY_DURESS_PASSWORD_3_HASH
            else -> return
        }
        val normalized = PasswordNormalizer.normalize(password)
        securePreferences.putStringSync(key, cryptoManager.hashPassword(normalized))
    }

    fun setDuressActions(index: Int, actions: List<SecurityAction>) {
        securePreferences.putStringSync("duress${index}_actions", JsonUtil.toJson(actions))
    }

    fun isMainPasswordSet(): Boolean =
        securePreferences.getString(Constants.KEY_MAIN_PASSWORD_HASH).isNotEmpty()

    fun isAnyPasswordSet(): Boolean =
        isMainPasswordSet() ||
            isDuressPasswordSet(1) ||
            isDuressPasswordSet(2) ||
            isDuressPasswordSet(3)

    fun isDuressPasswordSet(index: Int): Boolean = when (index) {
        1 -> securePreferences.getString(Constants.KEY_DURESS_PASSWORD_1_HASH).isNotEmpty()
        2 -> securePreferences.getString(Constants.KEY_DURESS_PASSWORD_2_HASH).isNotEmpty()
        3 -> securePreferences.getString(Constants.KEY_DURESS_PASSWORD_3_HASH).isNotEmpty()
        else -> false
    }

    fun validateNewPassword(
        plainPassword: String,
        forDuressIndex: Int? = null
    ): PasswordValidationError? {
        val normalized = PasswordNormalizer.normalize(plainPassword)
        if (normalized.length < 4) return PasswordValidationError.TOO_SHORT
        val config = getPasswordConfig()
        if (forDuressIndex != null) {
            if (config.mainPasswordHash.isNotEmpty() &&
                cryptoManager.verifyPassword(normalized, config.mainPasswordHash)
            ) {
                return PasswordValidationError.SAME_AS_MAIN
            }
        }
        val duressHashes = listOf(
            1 to config.duressPassword1Hash,
            2 to config.duressPassword2Hash,
            3 to config.duressPassword3Hash
        ).filter { (index, hash) ->
            hash.isNotEmpty() && index != forDuressIndex
        }
        if (duressHashes.any { (_, hash) ->
                cryptoManager.verifyPassword(normalized, hash)
            }
        ) {
            return PasswordValidationError.SAME_AS_OTHER_DURESS
        }
        return null
    }

    fun verifyPassword(password: String): PasswordVerificationResult {
        val normalized = PasswordNormalizer.normalize(password)
        if (normalized.isEmpty()) return PasswordVerificationResult(PasswordType.INVALID)
        val config = getPasswordConfig()
        return when {
            config.mainPasswordHash.isNotEmpty() &&
                cryptoManager.verifyPassword(normalized, config.mainPasswordHash) ->
                PasswordVerificationResult(PasswordType.MAIN)
            config.duressPassword1Hash.isNotEmpty() &&
                cryptoManager.verifyPassword(normalized, config.duressPassword1Hash) ->
                PasswordVerificationResult(PasswordType.DURESS_1, config.duress1Actions)
            config.duressPassword2Hash.isNotEmpty() &&
                cryptoManager.verifyPassword(normalized, config.duressPassword2Hash) ->
                PasswordVerificationResult(PasswordType.DURESS_2, config.duress2Actions)
            config.duressPassword3Hash.isNotEmpty() &&
                cryptoManager.verifyPassword(normalized, config.duressPassword3Hash) ->
                PasswordVerificationResult(PasswordType.DURESS_3, config.duress3Actions)
            else -> PasswordVerificationResult(PasswordType.INVALID)
        }
    }

    private fun loadActions(key: String): List<SecurityAction>? =
        JsonUtil.fromJsonOrNull(securePreferences.getString(key))
}

@Singleton
class BackupRepository @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val ruleRepository: RuleRepository,
    private val lockedAppRepository: LockedAppRepository,
    private val buttonTriggerRepository: ButtonTriggerRepository,
    private val decoyRepository: DecoyRepository,
    private val cryptoManager: CryptoManager
) {
    suspend fun exportBackup(): String {
        val backup = BackupData(
            profiles = profileRepository.observeAllProfiles().map { it }.let { flow ->
                var result = emptyList<Profile>()
                flow.collect { result = it }
                result
            },
            rules = ruleRepository.observeAllRules().let { flow ->
                var result = emptyList<Rule>()
                flow.collect { result = it }
                result
            },
            lockedApps = lockedAppRepository.observeAllLockedApps().let { flow ->
                var result = emptyList<LockedApp>()
                flow.collect { result = it }
                result
            },
            buttonTriggers = buttonTriggerRepository.observeAll().let { flow ->
                var result = emptyList<ButtonTrigger>()
                flow.collect { result = it }
                result
            },
            decoyContent = decoyRepository.observeAll().let { flow ->
                var result = emptyList<DecoyContent>()
                flow.collect { result = it }
                result
            }
        )
        return cryptoManager.encrypt(JsonUtil.toJson(backup))
    }

    suspend fun importBackup(encryptedPayload: String): Result<BackupData> = runCatching {
        val json = cryptoManager.decrypt(encryptedPayload)
        JsonUtil.fromJson<BackupData>(json)
    }
}
