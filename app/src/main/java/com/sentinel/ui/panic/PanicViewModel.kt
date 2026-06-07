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

    private val _lastActionKey = MutableStateFlow<String?>(null)
    val lastActionKey: StateFlow<String?> = _lastActionKey.asStateFlow()

    val config = PanicConfig()

    fun executeEmergencyProfile() = execute(
        listOf(SecurityAction(ActionType.ACTIVATE_PROFILE, mapOf("profileType" to "EMERGENCY"))),
        "action_emergency_profile"
    )

    fun lockAllApps() = execute(
        listOf(SecurityAction(ActionType.LOCK_APPLICATIONS)),
        "action_lock_apps"
    )

    fun activateDecoy() = execute(
        listOf(SecurityAction(ActionType.ACTIVATE_DECOY_MODE)),
        "action_decoy"
    )

    fun suppressNotifications() = execute(
        listOf(SecurityAction(ActionType.DISABLE_NOTIFICATIONS)),
        "action_notifications"
    )

    fun fullPanic() = execute(config.defaultActions, "action_full_panic")

    private fun execute(actions: List<SecurityAction>, messageKey: String) {
        viewModelScope.launch {
            triggerPanic(actions, EventSource.MANUAL)
            _lastActionKey.value = messageKey
        }
    }
}
