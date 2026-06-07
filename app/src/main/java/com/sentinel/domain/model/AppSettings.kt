package com.sentinel.domain.model

/**
 * Application settings (Module 11).
 */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val encryptionEnabled: Boolean = true,
    val autoBackupEnabled: Boolean = false,
    val backupIntervalHours: Int = 24,
    val intrusionDetectionEnabled: Boolean = true,
    val logRetentionDays: Int = 90,
    val accessibilityMonitoringEnabled: Boolean = false,
    val deviceAdminEnabled: Boolean = false,
    val onboardingCompleted: Boolean = false
)

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val profiles: List<Profile> = emptyList(),
    val rules: List<Rule> = emptyList(),
    val lockedApps: List<LockedApp> = emptyList(),
    val buttonTriggers: List<ButtonTrigger> = emptyList(),
    val decoyContent: List<DecoyContent> = emptyList(),
    val settings: AppSettings = AppSettings()
)

data class DashboardState(
    val activeProfile: Profile? = null,
    val securityStatus: SecurityStatus = SecurityStatus(),
    val activeRulesCount: Int = 0,
    val recentEvents: List<SecurityEvent> = emptyList(),
    val decoyModeActive: Boolean = false,
    val lockedAppsCount: Int = 0
)

data class SecurityStatus(
    val accessibilityEnabled: Boolean = false,
    val deviceAdminActive: Boolean = false,
    val encryptionActive: Boolean = true,
    val intrusionDetectionActive: Boolean = false,
    val overallScore: Int = 0
)
