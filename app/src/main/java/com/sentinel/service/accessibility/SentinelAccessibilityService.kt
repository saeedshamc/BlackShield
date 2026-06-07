package com.sentinel.service.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.sentinel.domain.model.EventSource
import com.sentinel.domain.model.SecurityEventInput
import com.sentinel.domain.model.TriggerType
import com.sentinel.domain.usecase.rule.ProcessSecurityEventUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Accessibility service for app monitoring, screen state, and automation (Module 10).
 */
@AndroidEntryPoint
class SentinelAccessibilityService : AccessibilityService() {

    @Inject lateinit var processSecurityEvent: ProcessSecurityEventUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var lastForegroundPackage: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val packageName = event.packageName?.toString() ?: return
                if (packageName != lastForegroundPackage) {
                    lastForegroundPackage = packageName
                    serviceScope.launch {
                        processSecurityEvent(
                            SecurityEventInput(
                                triggerType = TriggerType.APP_OPENED,
                                source = EventSource.ACCESSIBILITY_SERVICE,
                                payload = mapOf("packageName" to packageName)
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        // Service interrupted — no action required
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Accessibility service ready for app monitoring
    }
}
