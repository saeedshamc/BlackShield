package com.sentinel.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sentinel.domain.model.*
import com.sentinel.domain.usecase.rule.ProcessSecurityEventUseCase
import com.sentinel.data.repository.SecurityEventRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var securityEventRepository: SecurityEventRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        scope.launch {
            securityEventRepository.logEvent(
                SecurityEvent(
                    eventType = EventType.SETTINGS_CHANGED,
                    triggerSource = EventSource.BOOT_RECEIVER,
                    details = "Device booted — Sentinel services restored"
                )
            )
        }
    }
}

/** SIM card change detection (Module 9). */
@AndroidEntryPoint
class SimChangeReceiver : BroadcastReceiver() {

    @Inject lateinit var processSecurityEvent: ProcessSecurityEventUseCase
    @Inject lateinit var securityEventRepository: SecurityEventRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.intent.action.SIM_STATE_CHANGED") return
        val state = intent.extras?.getString("ss") ?: return

        if (state == "LOADED" || state == "ABSENT") {
            scope.launch {
                securityEventRepository.logEvent(
                    SecurityEvent(
                        eventType = EventType.SIM_CHANGED,
                        triggerSource = EventSource.SIM_RECEIVER,
                        details = "SIM state changed: $state",
                        severity = EventSeverity.WARNING
                    )
                )
                processSecurityEvent(
                    SecurityEventInput(
                        triggerType = TriggerType.SIM_CARD_CHANGED,
                        source = EventSource.SIM_RECEIVER,
                        payload = mapOf("simState" to state)
                    )
                )
            }
        }
    }
}

@AndroidEntryPoint
class ScreenStateReceiver : BroadcastReceiver() {

    @Inject lateinit var processSecurityEvent: ProcessSecurityEventUseCase
    @Inject lateinit var lockedAppDao: com.sentinel.data.local.dao.LockedAppDao

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        scope.launch {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    lockedAppDao.lockAll()
                    processSecurityEvent(
                        SecurityEventInput(
                            triggerType = TriggerType.SCREEN_OFF,
                            source = EventSource.SCREEN_RECEIVER
                        )
                    )
                }
                Intent.ACTION_SCREEN_ON -> {
                    processSecurityEvent(
                        SecurityEventInput(
                            triggerType = TriggerType.SCREEN_ON,
                            source = EventSource.SCREEN_RECEIVER
                        )
                    )
                }
            }
        }
    }
}
