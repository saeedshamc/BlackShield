package com.sentinel.ui.unlock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.domain.model.PasswordType
import com.sentinel.domain.usecase.security.VerifyPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnlockViewModel @Inject constructor(
    private val verifyPassword: VerifyPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnlockUiState())
    val uiState: StateFlow<UnlockUiState> = _uiState.asStateFlow()

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun submit(onSuccess: () -> Unit) {
        val password = _uiState.value.password
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = UnlockError.EMPTY)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = verifyPassword(password)
            when (result.type) {
                PasswordType.INVALID -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = UnlockError.INVALID,
                        password = ""
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, password = "")
                    onSuccess()
                }
            }
        }
    }
}

data class UnlockUiState(
    val password: String = "",
    val isLoading: Boolean = false,
    val error: UnlockError? = null
)

enum class UnlockError {
    EMPTY,
    INVALID
}
