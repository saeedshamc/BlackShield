package com.sentinel.util

object Constants {
    const val DATABASE_NAME = "sentinel.db"
    const val DATABASE_VERSION = 1

    const val PREFS_NAME = "sentinel_secure_prefs"
    const val DATASTORE_NAME = "sentinel_preferences"

    const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"
    const val KEY_DECOY_MODE_ACTIVE = "decoy_mode_active"
    const val KEY_MAIN_PASSWORD_HASH = "main_password_hash"
    const val KEY_DURESS_PASSWORD_1_HASH = "duress_password_1_hash"
    const val KEY_DURESS_PASSWORD_2_HASH = "duress_password_2_hash"
    const val KEY_DURESS_PASSWORD_3_HASH = "duress_password_3_hash"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_PANIC_FLOATING_ENABLED = "panic_floating_enabled"
    const val KEY_APP_LOCK_TIMEOUT = "app_lock_timeout_ms"
    const val KEY_NOTIFICATIONS_SUPPRESSED = "notifications_suppressed"

    const val DEFAULT_PROFILE_NORMAL = "normal"
    const val DEFAULT_PROFILE_TRAVEL = "travel"
    const val DEFAULT_PROFILE_BORDER = "border_crossing"
    const val DEFAULT_PROFILE_EMERGENCY = "emergency"

    const val ACTION_PANIC_TRIGGERED = "com.sentinel.action.PANIC_TRIGGERED"
    const val ACTION_PROFILE_SWITCHED = "com.sentinel.action.PROFILE_SWITCHED"
    const val ACTION_DECOY_ACTIVATED = "com.sentinel.action.DECOY_ACTIVATED"
    const val ACTION_LOCK_APPS = "com.sentinel.action.LOCK_APPS"
    const val ACTION_RULE_EXECUTED = "com.sentinel.action.RULE_EXECUTED"

    const val WORK_SECURITY_MONITOR = "security_monitor_work"
    const val WORK_BACKUP = "backup_work"

    const val MAX_FAILED_UNLOCK_ATTEMPTS = 5
    const val BUTTON_TRIGGER_WINDOW_MS = 3000L
}
