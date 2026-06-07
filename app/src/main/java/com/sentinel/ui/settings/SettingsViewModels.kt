package com.sentinel.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.data.local.datastore.PreferencesDataStore
import com.sentinel.data.repository.PasswordRepository
import com.sentinel.domain.model.ThemeMode
import com.sentinel.domain.usecase.backup.ExportBackupUseCase
import com.sentinel.domain.usecase.backup.ImportBackupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    preferencesDataStore: PreferencesDataStore
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = preferencesDataStore.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.DARK)
}

@HiltViewModel
class DuressPasswordViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun setMainPassword(password: String) {
        if (password.length < 4) {
            _message.value = "Password must be at least 4 characters"
            return
        }
        passwordRepository.setMainPassword(password)
        _message.value = "Main password saved"
    }

    fun setDuressPassword(index: Int, password: String) {
        if (password.length < 4) {
            _message.value = "Password must be at least 4 characters"
            return
        }
        passwordRepository.setDuressPassword(index, password)
        _message.value = "Duress password $index saved"
    }
}

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val exportBackup: ExportBackupUseCase,
    private val importBackup: ImportBackupUseCase
) : ViewModel() {

    private val _exportResult = MutableStateFlow<String?>(null)
    val exportResult: StateFlow<String?> = _exportResult.asStateFlow()

    private val _importResult = MutableStateFlow<String?>(null)
    val importResult: StateFlow<String?> = _importResult.asStateFlow()

    fun export() = viewModelScope.launch {
        runCatching {
            exportBackup()
        }.onSuccess {
            _exportResult.value = "Backup exported (${it.length} encrypted chars)"
        }.onFailure {
            _exportResult.value = "Export failed: ${it.message}"
        }
    }

    fun import(payload: String) = viewModelScope.launch {
        importBackup(payload).onSuccess {
            _importResult.value = "Backup imported: ${it.profiles.size} profiles, ${it.rules.size} rules"
        }.onFailure {
            _importResult.value = "Import failed: ${it.message}"
        }
    }
}
