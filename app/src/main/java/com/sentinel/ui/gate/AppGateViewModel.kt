package com.sentinel.ui.gate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.data.repository.PasswordRepository
import com.sentinel.domain.usecase.security.IsAppLockedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppGateViewModel @Inject constructor(
    private val isAppLocked: IsAppLockedUseCase,
    private val passwordRepository: PasswordRepository
) : ViewModel() {

    private val _needsUnlock = MutableStateFlow(true)
    val needsUnlock: StateFlow<Boolean> = _needsUnlock.asStateFlow()

    val hasPassword: Boolean get() = passwordRepository.isMainPasswordSet()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _needsUnlock.value = isAppLocked()
        }
    }
}
