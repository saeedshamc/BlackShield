package com.sentinel.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.data.local.datastore.PreferencesDataStore
import com.sentinel.data.repository.PasswordRepository
import com.sentinel.domain.model.AppLanguage
import com.sentinel.domain.model.ThemeMode
import com.sentinel.domain.usecase.backup.ExportBackupUseCase
import com.sentinel.domain.usecase.backup.ImportBackupUseCase
import com.sentinel.util.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = preferencesDataStore.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.DARK)

    val appLanguage: StateFlow<AppLanguage> = preferencesDataStore.appLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocaleManager.currentLanguage(context))

    fun setLanguage(language: AppLanguage, onApplied: () -> Unit) {
        viewModelScope.launch {
            LocaleManager.persist(context, language)
            preferencesDataStore.setAppLanguage(language)
            withContext(Dispatchers.Main.immediate) {
                onApplied()
            }
        }
    }
}

@HiltViewModel
class DuressPasswordViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _messageKey = MutableStateFlow<String?>(null)
    val messageKey: StateFlow<String?> = _messageKey.asStateFlow()
    private val _messageArg = MutableStateFlow<Int?>(null)
    val messageArg: StateFlow<Int?> = _messageArg.asStateFlow()

    fun setMainPassword(password: String) {
        if (password.length < 4) {
            _messageKey.value = "password_too_short"
            _messageArg.value = null
            return
        }
        passwordRepository.setMainPassword(password)
        _messageKey.value = "main_password_saved"
        _messageArg.value = null
    }

    fun setDuressPassword(index: Int, password: String) {
        if (password.length < 4) {
            _messageKey.value = "password_too_short"
            _messageArg.value = null
            return
        }
        passwordRepository.setDuressPassword(index, password)
        _messageKey.value = "duress_password_saved"
        _messageArg.value = index
    }
}

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val exportBackup: ExportBackupUseCase,
    private val importBackup: ImportBackupUseCase
) : ViewModel() {

    private val _exportKey = MutableStateFlow<String?>(null)
    val exportKey: StateFlow<String?> = _exportKey.asStateFlow()
    private val _exportArg = MutableStateFlow<String?>(null)
    val exportArg: StateFlow<String?> = _exportArg.asStateFlow()

    private val _importKey = MutableStateFlow<String?>(null)
    val importKey: StateFlow<String?> = _importKey.asStateFlow()
    private val _importArg1 = MutableStateFlow<Int?>(null)
    val importArg1: StateFlow<Int?> = _importArg1.asStateFlow()
    private val _importArg2 = MutableStateFlow<Int?>(null)
    val importArg2: StateFlow<Int?> = _importArg2.asStateFlow()
    private val _importError = MutableStateFlow<String?>(null)
    val importError: StateFlow<String?> = _importError.asStateFlow()

    fun export() = viewModelScope.launch {
        runCatching { exportBackup() }
            .onSuccess {
                _exportKey.value = "export_success"
                _exportArg.value = it.length.toString()
            }
            .onFailure {
                _exportKey.value = "export_failed"
                _exportArg.value = it.message
            }
    }

    fun import(payload: String) = viewModelScope.launch {
        importBackup(payload)
            .onSuccess {
                _importKey.value = "import_success"
                _importArg1.value = it.profiles.size
                _importArg2.value = it.rules.size
            }
            .onFailure {
                _importKey.value = "import_failed"
                _importArg1.value = null
                _importArg2.value = null
                _importError.value = it.message
            }
    }
}
