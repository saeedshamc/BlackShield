package com.sentinel.ui.panic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.domain.model.*
import com.sentinel.domain.usecase.security.TriggerPanicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PanicCenterViewModel @Inject constructor(
    private val triggerPanic: TriggerPanicUseCase
) : ViewModel() {

    private val _lastAction = MutableStateFlow<String?>(null)
    val lastAction: StateFlow<String?> = _lastAction.asStateFlow()

    val config = PanicConfig()

    fun executeEmergencyProfile() = execute(
        listOf(SecurityAction(ActionType.ACTIVATE_PROFILE, mapOf("profileType" to "EMERGENCY"))),
        "Emergency profile activated"
    )

    fun lockAllApps() = execute(
        listOf(SecurityAction(ActionType.LOCK_APPLICATIONS)),
        "All apps locked"
    )

    fun activateDecoy() = execute(
        listOf(SecurityAction(ActionType.ACTIVATE_DECOY_MODE)),
        "Decoy mode activated"
    )

    fun suppressNotifications() = execute(
        listOf(SecurityAction(ActionType.DISABLE_NOTIFICATIONS)),
        "Notifications suppressed"
    )

    fun fullPanic() = execute(config.defaultActions, "Full panic sequence executed")

    private fun execute(actions: List<SecurityAction>, message: String) {
        viewModelScope.launch {
            triggerPanic(actions, EventSource.MANUAL)
            _lastAction.value = message
        }
    }
}
