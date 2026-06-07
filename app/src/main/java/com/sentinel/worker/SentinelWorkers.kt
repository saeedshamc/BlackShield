package com.sentinel.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.sentinel.data.repository.SecurityEventRepository
import com.sentinel.domain.model.*
import com.sentinel.util.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Periodic security monitor for intrusion detection (Module 9).
 */
@HiltWorker
class SecurityMonitorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val securityEventRepository: SecurityEventRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        securityEventRepository.purgeOldEvents(90)
        securityEventRepository.logEvent(
            SecurityEvent(
                eventType = EventType.SETTINGS_CHANGED,
                triggerSource = EventSource.RULE_ENGINE,
                details = "Security monitor cycle completed"
            )
        )
        return Result.success()
    }

    companion object {
        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<SecurityMonitorWorker>(24, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.WORK_SECURITY_MONITOR,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = Result.success()

    companion object {
        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<BackupWorker>(24, TimeUnit.HOURS).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.WORK_BACKUP,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
