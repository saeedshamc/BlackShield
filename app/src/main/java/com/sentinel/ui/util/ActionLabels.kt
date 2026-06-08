package com.sentinel.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sentinel.R
import com.sentinel.domain.model.ActionType
import com.sentinel.domain.model.TriggerType

object ActionLabels {

    @StringRes
    fun triggerLabel(type: TriggerType): Int = when (type) {
        TriggerType.WRONG_PASSWORD_ATTEMPTS -> R.string.trigger_wrong_password
        TriggerType.POWER_BUTTON_PRESS -> R.string.trigger_power_button
        TriggerType.VOLUME_BUTTON_COMBO -> R.string.trigger_volume_combo
        TriggerType.SECRET_PIN_ENTERED -> R.string.trigger_secret_pin
        TriggerType.SIM_CARD_CHANGED -> R.string.trigger_sim_changed
        TriggerType.APP_OPENED -> R.string.trigger_app_opened
        TriggerType.SCREEN_OFF -> R.string.trigger_screen_off
        TriggerType.SCREEN_ON -> R.string.trigger_screen_on
        TriggerType.UNLOCK_FAILURE -> R.string.trigger_unlock_failure
        TriggerType.ACCESSIBILITY_DISABLED -> R.string.trigger_accessibility_disabled
        TriggerType.DEVICE_ADMIN_REMOVED -> R.string.trigger_device_admin_removed
        TriggerType.CUSTOM_GESTURE -> R.string.trigger_custom_gesture
        TriggerType.TIME_BASED -> R.string.trigger_time_based
        TriggerType.MANUAL -> R.string.trigger_manual
    }

    @StringRes
    fun actionLabel(type: ActionType): Int = when (type) {
        ActionType.ACTIVATE_PROFILE -> R.string.action_activate_profile
        ActionType.LOCK_APPLICATIONS -> R.string.action_lock_apps
        ActionType.HIDE_APPLICATIONS -> R.string.action_hide_apps
        ActionType.ACTIVATE_DECOY_MODE -> R.string.action_activate_decoy
        ActionType.EXECUTE_EMERGENCY_ACTIONS -> R.string.action_emergency
        ActionType.DISABLE_NOTIFICATIONS -> R.string.action_disable_notifications
        ActionType.LOG_EVENT -> R.string.action_log_event
        ActionType.WIPE_DECOY_DATA -> R.string.action_wipe_decoy
        ActionType.DELETE_SELECTED_FILES -> R.string.action_delete_files
        ActionType.CLEAR_SECURITY_LOGS -> R.string.action_clear_logs
        ActionType.LOCK_DEVICE -> R.string.action_lock_device
        ActionType.CUSTOM -> R.string.action_custom
    }

    @Composable
    fun triggerName(type: TriggerType): String = stringResource(triggerLabel(type))

    @Composable
    fun actionName(type: ActionType): String = stringResource(actionLabel(type))
}
