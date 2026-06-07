package com.sentinel.service.panic

import android.content.Intent
import android.view.KeyEvent
import com.sentinel.data.repository.ButtonTriggerRepository
import com.sentinel.domain.model.*
import com.sentinel.domain.usecase.rule.ProcessSecurityEventUseCase
import com.sentinel.domain.usecase.security.TriggerPanicUseCase
import com.sentinel.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Hardware button trigger monitor (Module 7).
 * Detects power/volume combinations and executes configured rules.
 */
@AndroidEntryPoint
class ButtonTriggerService : android.app.Service() {

    @Inject lateinit var processSecurityEvent: ProcessSecurityEventUseCase
    @Inject lateinit var triggerPanic: TriggerPanicUseCase
    @Inject lateinit var buttonTriggerRepository: ButtonTriggerRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val powerPressTimes = mutableListOf<Long>()

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleKeyEvent(it) }
        return START_STICKY
    }

    private fun handleKeyEvent(intent: Intent) {
        val keyCode = intent.getIntExtra("key_code", -1)
        val action = intent.getIntExtra("key_action", KeyEvent.ACTION_DOWN)

        if (action != KeyEvent.ACTION_DOWN) return

        when (keyCode) {
            KeyEvent.KEYCODE_POWER -> handlePowerPress()
            KeyEvent.KEYCODE_VOLUME_UP -> handleVolumeCombo("POWER_VOLUME_UP")
            KeyEvent.KEYCODE_VOLUME_DOWN -> handleVolumeCombo("POWER_VOLUME_DOWN")
        }
    }

    private fun handlePowerPress() {
        val now = System.currentTimeMillis()
        powerPressTimes.add(now)
        powerPressTimes.removeAll { now - it > Constants.BUTTON_TRIGGER_WINDOW_MS }

        val count = powerPressTimes.size
        scope.launch {
            processSecurityEvent(
                SecurityEventInput(
                    triggerType = TriggerType.POWER_BUTTON_PRESS,
                    source = EventSource.BUTTON_TRIGGER_SERVICE,
                    payload = mapOf("pressCount" to count.toString())
                )
            )

            if (count >= 5) {
                triggerPanic(PanicConfig().defaultActions, EventSource.BUTTON_TRIGGER_SERVICE)
                powerPressTimes.clear()
            }

            val triggers = buttonTriggerRepository.observeEnabled().first()
            triggers.filter { it.combination == ButtonCombination.POWER_X5 && count >= 5 }
                .forEach { trigger ->
                    triggerPanic(trigger.actions, EventSource.BUTTON_TRIGGER_SERVICE)
                }
        }
    }

    private fun handleVolumeCombo(combo: String) {
        scope.launch {
            processSecurityEvent(
                SecurityEventInput(
                    triggerType = TriggerType.VOLUME_BUTTON_COMBO,
                    source = EventSource.BUTTON_TRIGGER_SERVICE,
                    payload = mapOf("combination" to combo)
                )
            )
        }
    }
}
