package com.sentinel

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.sentinel.domain.usecase.decoy.EnsureDecoyDefaultsUseCase
import com.sentinel.domain.usecase.profile.EnsureDefaultProfilesUseCase
import com.sentinel.util.LocaleManager
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
        }
    }
}
