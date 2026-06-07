package com.sentinel.service.panic

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.sentinel.domain.model.*
import com.sentinel.domain.usecase.security.TriggerPanicUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/** Quick Settings panic tile (Module 6). */
@AndroidEntryPoint
class PanicTileService : TileService() {

    @Inject lateinit var triggerPanic: TriggerPanicUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.apply {
            state = Tile.STATE_ACTIVE
            label = "SENTINEL PANIC"
            updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        scope.launch {
            triggerPanic(
                PanicConfig().defaultActions,
                EventSource.PANIC_TILE
            )
        }
    }
}

/** Floating panic button overlay service (Module 6). */
@AndroidEntryPoint
class PanicOverlayService : android.app.Service() {

    @Inject lateinit var triggerPanic: TriggerPanicUseCase

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Overlay implementation would use WindowManager — stub for foreground service lifecycle
        return START_STICKY
    }
}
