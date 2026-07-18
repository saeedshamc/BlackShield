package com.sentinel

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import com.sentinel.util.LocaleManager
import androidx.work.Configuration
import com.sentinel.data.repository.PasswordRepository
import com.sentinel.domain.usecase.decoy.EnsureDecoyDefaultsUseCase
import com.sentinel.domain.usecase.profile.EnsureDefaultProfilesUseCase
import com.sentinel.domain.usecase.security.LockAppUseCase
import com.sentinel.service.lock.DeviceLockServiceManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SentinelApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var applicationScope: CoroutineScope
    @Inject lateinit var ensureDefaultProfiles: EnsureDefaultProfilesUseCase
    @Inject lateinit var ensureDecoyDefaults: EnsureDecoyDefaultsUseCase
    @Inject lateinit var lockApp: LockAppUseCase
    @Inject lateinit var passwordRepository: PasswordRepository
    @Inject lateinit var deviceLockServiceManager: DeviceLockServiceManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        LocaleManager.restore(this)
        applicationScope.launch {
            ensureDefaultProfiles()
            ensureDecoyDefaults()
            if (passwordRepository.isAnyPasswordSet()) {
                lockApp()
            }
            deviceLockServiceManager.syncServiceState()
        }
    }
}
