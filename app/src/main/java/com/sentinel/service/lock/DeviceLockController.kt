package com.sentinel.service.lock

import android.content.Context
import android.content.Intent
import com.sentinel.data.local.datastore.PreferencesDataStore
import com.sentinel.data.repository.PasswordRepository
import com.sentinel.ui.lock.DeviceLockActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceLockController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesDataStore: PreferencesDataStore,
    private val passwordRepository: PasswordRepository,
    private val serviceManager: DeviceLockServiceManager
) {
    val deviceLockEnabled: Flow<Boolean> = preferencesDataStore.deviceLockEnabled

    suspend fun shouldShowLock(): Boolean {
        if (!preferencesDataStore.deviceLockEnabled.first()) return false
        if (!passwordRepository.isAnyPasswordSet()) return false
        return !preferencesDataStore.deviceSessionUnlocked.first()
    }

    suspend fun lockSession() {
        preferencesDataStore.setDeviceSessionUnlocked(false)
        preferencesDataStore.lockApp()
    }

    suspend fun unlockSession() {
        preferencesDataStore.setDeviceSessionUnlocked(true)
    }

    suspend fun setEnabled(enabled: Boolean) {
        preferencesDataStore.setDeviceLockEnabled(enabled)
        if (enabled) {
            lockSession()
        }
        serviceManager.syncServiceState()
    }

    fun presentLockScreen() {
        val intent = Intent(context, DeviceLockActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }
        context.startActivity(intent)
    }
}
