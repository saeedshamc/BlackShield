package com.sentinel.service.lock

import android.content.Context
import com.sentinel.data.local.datastore.PreferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceLockServiceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesDataStore: PreferencesDataStore
) {
    suspend fun syncServiceState() {
        if (preferencesDataStore.deviceLockEnabled.first()) {
            DeviceLockMonitorService.start(context)
        } else {
            DeviceLockMonitorService.stop(context)
        }
    }
}
