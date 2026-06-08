package com.sentinel.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.data.repository.PasswordRepository
import com.sentinel.data.repository.SensitiveFilesRepository
import com.sentinel.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuressConfigViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository,
    private val sensitiveFilesRepository: SensitiveFilesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DuressConfigUiState())
    val uiState: StateFlow<DuressConfigUiState> = _uiState.asStateFlow()

    private val _messageKey = MutableStateFlow<String?>(null)
    val messageKey: StateFlow<String?> = _messageKey.asStateFlow()

    init {
        load()
    }

    fun load() {
        val config = passwordRepository.getPasswordConfig()
        _uiState.value = DuressConfigUiState(
            duress1Actions = config.duress1Actions.map { it.type }.toSet(),
            duress2Actions = config.duress2Actions.map { it.type }.toSet(),
            duress3Actions = config.duress3Actions.map { it.type }.toSet(),
            duress1Profile = config.duress1Actions
                .find { it.type == ActionType.ACTIVATE_PROFILE }?.parameters?.get("profileType") ?: "EMERGENCY",
            duress2Profile = config.duress2Actions
                .find { it.type == ActionType.ACTIVATE_PROFILE }?.parameters?.get("profileType") ?: "EMERGENCY",
            duress3Profile = config.duress3Actions
                .find { it.type == ActionType.ACTIVATE_PROFILE }?.parameters?.get("profileType") ?: "EMERGENCY",
            sensitiveFiles = sensitiveFilesRepository.getAll(),
            mainPasswordSet = passwordRepository.isMainPasswordSet(),
            duress1Set = passwordRepository.isDuressPasswordSet(1),
            duress2Set = passwordRepository.isDuressPasswordSet(2),
            duress3Set = passwordRepository.isDuressPasswordSet(3)
        )
    }

    fun clearMessage() {
        _messageKey.value = null
    }

    fun updateMainPassword(value: String) = _uiState.update { it.copy(mainPassword = value) }

    fun updateDuressPassword(index: Int, value: String) = _uiState.update {
        when (index) {
            1 -> it.copy(duress1Password = value)
            2 -> it.copy(duress2Password = value)
            else -> it.copy(duress3Password = value)
        }
    }

    fun updateProfile(index: Int, profile: String) = _uiState.update { state ->
        when (index) {
            1 -> state.copy(duress1Profile = profile)
            2 -> state.copy(duress2Profile = profile)
            else -> state.copy(duress3Profile = profile)
        }
    }

    fun toggleAction(index: Int, action: ActionType) = _uiState.update { state ->
        when (index) {
            1 -> state.copy(duress1Actions = state.duress1Actions.toggle(action))
            2 -> state.copy(duress2Actions = state.duress2Actions.toggle(action))
            else -> state.copy(duress3Actions = state.duress3Actions.toggle(action))
        }
    }

    fun saveMainPassword() {
        val pwd = _uiState.value.mainPassword
        val error = passwordRepository.validateNewPassword(pwd)
        if (error != null) {
            _messageKey.value = error.name.lowercase()
            return
        }
        passwordRepository.setMainPassword(pwd)
        _uiState.update { it.copy(mainPassword = "") }
        _messageKey.value = "main_password_saved"
        load()
    }

    fun saveDuress(index: Int) {
        val state = _uiState.value
        if (!state.mainPasswordSet) {
            _messageKey.value = "main_password_required_first"
            return
        }

        val pwd = duressPassword(state, index)
        val actions = duressActions(state, index)
        val alreadySet = duressAlreadySet(state, index)

        if (actions.isEmpty()) {
            _messageKey.value = PasswordValidationError.NO_ACTIONS.name.lowercase()
            return
        }

        if (pwd.isBlank()) {
            if (!alreadySet) {
                _messageKey.value = PasswordValidationError.REQUIRED.name.lowercase()
                return
            }
        } else {
            val error = passwordRepository.validateNewPassword(pwd, forDuressIndex = index)
            if (error != null) {
                _messageKey.value = error.name.lowercase()
                return
            }
            passwordRepository.setDuressPassword(index, pwd)
        }

        val profile = duressProfile(state, index)
        passwordRepository.setDuressActions(index, buildActions(actions, profile))

        _uiState.update {
            when (index) {
                1 -> it.copy(duress1Password = "")
                2 -> it.copy(duress2Password = "")
                else -> it.copy(duress3Password = "")
            }
        }
        _messageKey.value = "duress_config_saved"
        load()
    }

    fun addSensitiveFileFromUri(
        displayName: String,
        uri: String,
        isFolder: Boolean,
        assignToDuress: Int? = null
    ) {
        if (displayName.isBlank() || uri.isBlank()) return
        viewModelScope.launch {
            sensitiveFilesRepository.add(
                SensitiveFileTarget(
                    displayName = displayName,
                    filePath = uri,
                    isFolder = isFolder,
                    deleteOnDuress1 = assignToDuress == 1,
                    deleteOnDuress2 = assignToDuress == 2,
                    deleteOnDuress3 = assignToDuress == 3
                )
            )
            _messageKey.value = "file_added"
            load()
        }
    }

    fun removeSensitiveFile(id: String) {
        viewModelScope.launch {
            sensitiveFilesRepository.remove(id)
            load()
        }
    }

    fun toggleFileDuress(fileId: String, duressIndex: Int) {
        viewModelScope.launch {
            val files = sensitiveFilesRepository.getAll()
            val updated = files.map { file ->
                if (file.id != fileId) file else when (duressIndex) {
                    1 -> file.copy(deleteOnDuress1 = !file.deleteOnDuress1)
                    2 -> file.copy(deleteOnDuress2 = !file.deleteOnDuress2)
                    else -> file.copy(deleteOnDuress3 = !file.deleteOnDuress3)
                }
            }
            sensitiveFilesRepository.saveAll(updated)
            load()
        }
    }

    private fun buildActions(selected: Set<ActionType>, profile: String): List<SecurityAction> =
        selected.map { type ->
            if (type == ActionType.ACTIVATE_PROFILE) {
                SecurityAction(type, mapOf("profileType" to profile))
            } else {
                SecurityAction(type)
            }
        }

    private fun duressPassword(state: DuressConfigUiState, index: Int) = when (index) {
        1 -> state.duress1Password
        2 -> state.duress2Password
        else -> state.duress3Password
    }

    private fun duressActions(state: DuressConfigUiState, index: Int) = when (index) {
        1 -> state.duress1Actions
        2 -> state.duress2Actions
        else -> state.duress3Actions
    }

    private fun duressProfile(state: DuressConfigUiState, index: Int) = when (index) {
        1 -> state.duress1Profile
        2 -> state.duress2Profile
        else -> state.duress3Profile
    }

    private fun duressAlreadySet(state: DuressConfigUiState, index: Int) = when (index) {
        1 -> state.duress1Set
        2 -> state.duress2Set
        else -> state.duress3Set
    }

    private fun MutableStateFlow<DuressConfigUiState>.update(block: (DuressConfigUiState) -> DuressConfigUiState) {
        value = block(value)
    }

    private fun Set<ActionType>.toggle(action: ActionType): Set<ActionType> =
        if (contains(action)) minus(action) else plus(action)
}

data class DuressConfigUiState(
    val mainPassword: String = "",
    val duress1Password: String = "",
    val duress2Password: String = "",
    val duress3Password: String = "",
    val duress1Actions: Set<ActionType> = setOf(ActionType.ACTIVATE_DECOY_MODE),
    val duress2Actions: Set<ActionType> = setOf(ActionType.LOCK_APPLICATIONS),
    val duress3Actions: Set<ActionType> = setOf(
        ActionType.DELETE_SELECTED_FILES,
        ActionType.WIPE_DECOY_DATA
    ),
    val duress1Profile: String = "EMERGENCY",
    val duress2Profile: String = "EMERGENCY",
    val duress3Profile: String = "EMERGENCY",
    val sensitiveFiles: List<SensitiveFileTarget> = emptyList(),
    val mainPasswordSet: Boolean = false,
    val duress1Set: Boolean = false,
    val duress2Set: Boolean = false,
    val duress3Set: Boolean = false
)
