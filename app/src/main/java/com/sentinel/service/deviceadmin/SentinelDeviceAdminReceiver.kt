package com.sentinel.service.deviceadmin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.sentinel.domain.model.EventSource
import com.sentinel.domain.model.EventType
import com.sentinel.domain.model.EventSeverity
import com.sentinel.domain.model.SecurityEvent
import com.sentinel.domain.model.TriggerType
import com.sentinel.domain.model.SecurityEventInput
import com.sentinel.data.repository.SecurityEventRepository
import com.sentinel.domain.usecase.rule.ProcessSecurityEventUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Device admin receiver for intrusion detection (Module 9).
 */
@AndroidEntryPoint
class SentinelDeviceAdminReceiver : DeviceAdminReceiver() {

    @Inject lateinit var processSecurityEvent: ProcessSecurityEventUseCase
    @Inject lateinit var securityEventRepository: SecurityEventRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        scope.launch {
            securityEventRepository.logEvent(
                SecurityEvent(
                    eventType = EventType.DEVICE_ADMIN_REMOVED,
                    triggerSource = EventSource.DEVICE_ADMIN,
                    details = "Device admin was disabled — potential tampering",
                    severity = EventSeverity.CRITICAL
                )
            )
            processSecurityEvent(
                SecurityEventInput(
                    triggerType = TriggerType.DEVICE_ADMIN_REMOVED,
                    source = EventSource.DEVICE_ADMIN
                )
            )
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        scope.launch {
            securityEventRepository.logEvent(
                SecurityEvent(
                    eventType = EventType.SETTINGS_CHANGED,
                    triggerSource = EventSource.DEVICE_ADMIN,
                    details = "Device admin enabled"
                )
            )
        }
    }
}

object DeviceAdminManager {
    const val COMPONENT_NAME = "com.sentinel/.service.deviceadmin.SentinelDeviceAdminReceiver"
}
