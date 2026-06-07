package com.sentinel.domain.model

/**
 * Emergency profile configuration (Module 2).
 * Switching profiles instantly applies all associated security settings.
 */
data class Profile(
    val id: Long = 0,
    val name: String,
    val type: ProfileType,
    val isDefault: Boolean = false,
    val isActive: Boolean = false,
    val hiddenApps: List<String> = emptyList(),
    val lockedApps: List<String> = emptyList(),
    val notificationBehavior: NotificationBehavior = NotificationBehavior.NORMAL,
    val securityRules: List<Long> = emptyList(),
    val decoySettings: DecoySettings = DecoySettings(),
    val color: String = "#00E5FF",
    val icon: String = "shield",
    val createdAt: Long = System.currentTimeMillis()
)

enum class ProfileType {
    NORMAL,
    TRAVEL,
    BORDER_CROSSING,
    EMERGENCY,
    CUSTOM
}

enum class NotificationBehavior {
    NORMAL,
    SILENT,
    DECOY_ONLY,
    SUPPRESSED
}

data class DecoySettings(
    val enabled: Boolean = false,
    val showFakeGallery: Boolean = true,
    val showFakeContacts: Boolean = true,
    val showFakeNotes: Boolean = true,
    val showFakeFiles: Boolean = true,
    val showFakeRecentActivity: Boolean = true
)
