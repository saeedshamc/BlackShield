package com.sentinel.domain.usecase.security

import com.sentinel.data.repository.PasswordRepository
import com.sentinel.data.repository.SecurityEventRepository
import com.sentinel.domain.model.*
import com.sentinel.engine.ActionExecutor
import com.sentinel.engine.RuleEngine
import com.sentinel.data.local.datastore.PreferencesDataStore
import com.sentinel.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ObserveSecurityEventsUseCase @Inject constructor(
    private val repository: SecurityEventRepository
) {
    operator fun invoke(): Flow<List<SecurityEvent>> = repository.observeAllEvents()
    fun recent(limit: Int = 10): Flow<List<SecurityEvent>> = repository.observeRecentEvents(limit)
    fun search(query: String): Flow<List<SecurityEvent>> = repository.searchEvents(query)
}

class VerifyPasswordUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository,
    private val actionExecutor: ActionExecutor,
    private val preferencesDataStore: PreferencesDataStore,
    private val ruleEngine: RuleEngine
) {
    suspend operator fun invoke(password: String): PasswordVerificationResult {
        val result = passwordRepository.verifyPassword(password)
        when (result.type) {
            PasswordType.INVALID -> {
                preferencesDataStore.incrementFailedUnlockCount()
                val count = preferencesDataStore.failedUnlockCount.first()
                if (count >= Constants.MAX_FAILED_UNLOCK_ATTEMPTS) {
                    ruleEngine.processEvent(
                        SecurityEventInput(
                            triggerType = TriggerType.WRONG_PASSWORD_ATTEMPTS,
                            source = EventSource.DURESS_PASSWORD,
                            payload = mapOf("attemptCount" to count.toString())
                        )
                    )
                }
            }
            PasswordType.MAIN -> preferencesDataStore.resetFailedUnlockCount()
            PasswordType.DURESS_1,
            PasswordType.DURESS_2,
            PasswordType.DURESS_3 -> {
                actionExecutor.executeActions(result.actions, EventSource.DURESS_PASSWORD)
            }
        }
        return result
    }
}

class TriggerPanicUseCase @Inject constructor(
    private val actionExecutor: ActionExecutor
) {
    suspend operator fun invoke(actions: List<SecurityAction>, source: EventSource) {
        actionExecutor.executeActions(actions, source)
    }
}
