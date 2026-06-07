package com.sentinel.engine

import com.sentinel.domain.model.*
import com.sentinel.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Evaluates whether an incoming event matches a rule's trigger definition.
 */
@Singleton
class TriggerEvaluator @Inject constructor() {

    fun matches(rule: Rule, event: SecurityEventInput): Boolean {
        if (rule.trigger.type != event.triggerType) return false
        return when (rule.trigger.type) {
            TriggerType.WRONG_PASSWORD_ATTEMPTS -> {
                val threshold = rule.trigger.intParam("threshold", Constants.MAX_FAILED_UNLOCK_ATTEMPTS)
                event.payload["attemptCount"]?.toIntOrNull()?.let { it >= threshold } ?: false
            }
            TriggerType.POWER_BUTTON_PRESS -> {
                val required = rule.trigger.intParam("count", 5)
                event.payload["pressCount"]?.toIntOrNull()?.let { it >= required } ?: false
            }
            TriggerType.VOLUME_BUTTON_COMBO -> {
                event.payload["combination"] == rule.trigger.stringParam("combination")
            }
            TriggerType.SECRET_PIN_ENTERED -> {
                event.payload["pin"] == rule.trigger.stringParam("pin")
            }
            TriggerType.SIM_CARD_CHANGED -> true
            TriggerType.APP_OPENED -> {
                event.payload["packageName"] == rule.trigger.stringParam("packageName")
            }
            TriggerType.SCREEN_OFF, TriggerType.SCREEN_ON -> true
            TriggerType.UNLOCK_FAILURE -> {
                val threshold = rule.trigger.intParam("threshold", 3)
                event.payload["attemptCount"]?.toIntOrNull()?.let { it >= threshold } ?: false
            }
            TriggerType.ACCESSIBILITY_DISABLED,
            TriggerType.DEVICE_ADMIN_REMOVED -> true
            TriggerType.CUSTOM_GESTURE -> {
                event.payload["gestureId"] == rule.trigger.stringParam("gestureId")
            }
            TriggerType.TIME_BASED -> evaluateTimeRange(rule.trigger)
            TriggerType.MANUAL -> event.source == EventSource.MANUAL
        }
    }

    private fun evaluateTimeRange(trigger: Trigger): Boolean {
        val startHour = trigger.intParam("startHour", 0)
        val endHour = trigger.intParam("endHour", 23)
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return if (startHour <= endHour) {
            currentHour in startHour..endHour
        } else {
            currentHour >= startHour || currentHour <= endHour
        }
    }
}
