package com.sentinel.domain.model

/**
 * Domain model representing an automation rule.
 * Pipeline: Trigger → Condition(s) → Action(s)
 */
data class Rule(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val enabled: Boolean = true,
    val priority: Int = 0,
    val trigger: Trigger,
    val conditions: List<Condition> = emptyList(),
    val actions: List<SecurityAction> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class TriggerType {
    WRONG_PASSWORD_ATTEMPTS,
    POWER_BUTTON_PRESS,
    VOLUME_BUTTON_COMBO,
    SECRET_PIN_ENTERED,
    SIM_CARD_CHANGED,
    APP_OPENED,
    SCREEN_OFF,
    SCREEN_ON,
    UNLOCK_FAILURE,
    ACCESSIBILITY_DISABLED,
    DEVICE_ADMIN_REMOVED,
    CUSTOM_GESTURE,
    TIME_BASED,
    MANUAL
}

data class Trigger(
    val type: TriggerType,
    val parameters: Map<String, String> = emptyMap()
) {
    fun intParam(key: String, default: Int = 0): Int =
        parameters[key]?.toIntOrNull() ?: default

    fun stringParam(key: String, default: String = ""): String =
        parameters[key] ?: default
}

enum class ConditionOperator {
    AND,
    OR
}

enum class ConditionType {
    THRESHOLD_EXCEEDED,
    APP_IS_RUNNING,
    PROFILE_IS_ACTIVE,
    TIME_IN_RANGE,
    SIM_PRESENT,
    BIOMETRIC_AVAILABLE,
    CUSTOM
}

data class Condition(
    val type: ConditionType,
    val operator: ConditionOperator = ConditionOperator.AND,
    val parameters: Map<String, String> = emptyMap()
) {
    fun intParam(key: String, default: Int = 0): Int =
        parameters[key]?.toIntOrNull() ?: default
}

enum class ActionType {
    ACTIVATE_PROFILE,
    LOCK_APPLICATIONS,
    HIDE_APPLICATIONS,
    ACTIVATE_DECOY_MODE,
    EXECUTE_EMERGENCY_ACTIONS,
    DISABLE_NOTIFICATIONS,
    LOG_EVENT,
    WIPE_DECOY_DATA,
    DELETE_SELECTED_FILES,
    CLEAR_SECURITY_LOGS,
    LOCK_DEVICE,
    CUSTOM
}

data class SecurityAction(
    val type: ActionType,
    val parameters: Map<String, String> = emptyMap()
)

/** Incoming event from triggers (accessibility, receivers, services). */
data class SecurityEventInput(
    val triggerType: TriggerType,
    val source: EventSource,
    val payload: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class EventSource {
    ACCESSIBILITY_SERVICE,
    DEVICE_ADMIN,
    SIM_RECEIVER,
    SCREEN_RECEIVER,
    BUTTON_TRIGGER_SERVICE,
    PANIC_TILE,
    PANIC_OVERLAY,
    DURESS_PASSWORD,
    RULE_ENGINE,
    MANUAL,
    BOOT_RECEIVER
}

enum class EventType {
    RULE_EXECUTED,
    PROFILE_SWITCHED,
    DECOY_ACTIVATED,
    DECOY_DEACTIVATED,
    APP_LOCKED,
    APP_UNLOCKED,
    PANIC_TRIGGERED,
    INTRUSION_DETECTED,
    SIM_CHANGED,
    UNLOCK_FAILURE,
    ACCESSIBILITY_DISABLED,
    DEVICE_ADMIN_REMOVED,
    PASSWORD_DURESS,
    BUTTON_TRIGGER,
    SETTINGS_CHANGED,
    BACKUP_CREATED,
    BACKUP_RESTORED
}

data class SecurityEvent(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: EventType,
    val ruleId: Long? = null,
    val ruleName: String? = null,
    val triggerSource: EventSource,
    val details: String = "",
    val severity: EventSeverity = EventSeverity.INFO
)

enum class EventSeverity {
    INFO,
    WARNING,
    CRITICAL
}
