package com.sentinel.domain.model

/**
 * Hardware button trigger configuration (Module 7).
 */
data class ButtonTrigger(
    val id: Long = 0,
    val name: String,
    val combination: ButtonCombination,
    val enabled: Boolean = true,
    val ruleId: Long? = null,
    val actions: List<SecurityAction> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class ButtonCombination {
    POWER_X3,
    POWER_X5,
    POWER_VOLUME_UP,
    POWER_VOLUME_DOWN,
    VOLUME_UP_DOWN,
    LONG_PRESS_POWER,
    LONG_PRESS_POWER_VOLUME_UP,
    CUSTOM
}

data class PanicConfig(
    val quickSettingsTileEnabled: Boolean = true,
    val floatingButtonEnabled: Boolean = false,
    val powerButtonComboEnabled: Boolean = true,
    val volumeButtonComboEnabled: Boolean = false,
    val secretGestureEnabled: Boolean = false,
    val defaultActions: List<SecurityAction> = listOf(
        SecurityAction(ActionType.ACTIVATE_PROFILE, mapOf("profileType" to "EMERGENCY")),
        SecurityAction(ActionType.LOCK_APPLICATIONS),
        SecurityAction(ActionType.DISABLE_NOTIFICATIONS)
    )
)
