package com.sentinel.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.sentinel.domain.model.ThemeMode
import com.sentinel.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.DATASTORE_NAME
)

@Singleton
class PreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACTIVE_PROFILE_ID = longPreferencesKey(Constants.KEY_ACTIVE_PROFILE_ID)
        val DECOY_MODE_ACTIVE = booleanPreferencesKey(Constants.KEY_DECOY_MODE_ACTIVE)
        val THEME_MODE = stringPreferencesKey(Constants.KEY_THEME_MODE)
        val PANIC_FLOATING_ENABLED = booleanPreferencesKey(Constants.KEY_PANIC_FLOATING_ENABLED)
        val APP_LOCK_TIMEOUT = longPreferencesKey(Constants.KEY_APP_LOCK_TIMEOUT)
        val NOTIFICATIONS_SUPPRESSED = booleanPreferencesKey(Constants.KEY_NOTIFICATIONS_SUPPRESSED)
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val INTRUSION_DETECTION = booleanPreferencesKey("intrusion_detection_enabled")
        val AUTO_BACKUP = booleanPreferencesKey("auto_backup_enabled")
        val LOG_RETENTION_DAYS = intPreferencesKey("log_retention_days")
        val FAILED_UNLOCK_COUNT = intPreferencesKey("failed_unlock_count")
    }

    val activeProfileId: Flow<Long> = context.dataStore.data.map {
        it[Keys.ACTIVE_PROFILE_ID] ?: 1L
    }

    val decoyModeActive: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.DECOY_MODE_ACTIVE] ?: false
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map {
        runCatching { ThemeMode.valueOf(it[Keys.THEME_MODE] ?: ThemeMode.DARK.name) }
            .getOrDefault(ThemeMode.DARK)
    }

    val notificationsSuppressed: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.NOTIFICATIONS_SUPPRESSED] ?: false
    }

    val failedUnlockCount: Flow<Int> = context.dataStore.data.map {
        it[Keys.FAILED_UNLOCK_COUNT] ?: 0
    }

    suspend fun setActiveProfileId(id: Long) = edit { it[Keys.ACTIVE_PROFILE_ID] = id }
    suspend fun setDecoyModeActive(active: Boolean) = edit { it[Keys.DECOY_MODE_ACTIVE] = active }
    suspend fun setThemeMode(mode: ThemeMode) = edit { it[Keys.THEME_MODE] = mode.name }
    suspend fun setPanicFloatingEnabled(enabled: Boolean) = edit { it[Keys.PANIC_FLOATING_ENABLED] = enabled }
    suspend fun setNotificationsSuppressed(suppressed: Boolean) = edit { it[Keys.NOTIFICATIONS_SUPPRESSED] = suppressed }
    suspend fun setOnboardingCompleted(completed: Boolean) = edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    suspend fun incrementFailedUnlockCount() = edit {
        it[Keys.FAILED_UNLOCK_COUNT] = (it[Keys.FAILED_UNLOCK_COUNT] ?: 0) + 1
    }
    suspend fun resetFailedUnlockCount() = edit { it[Keys.FAILED_UNLOCK_COUNT] = 0 }

    private suspend fun edit(transform: suspend (MutablePreferences) -> Unit) {
        context.dataStore.edit { transform(it) }
    }
}
