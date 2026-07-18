package com.sentinel.ui.lock

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.sentinel.service.lock.DeviceLockController
import com.sentinel.ui.base.LocaleAwareComponentActivity
import com.sentinel.ui.theme.SentinelLocaleProvider
import com.sentinel.ui.theme.SentinelTheme
import com.sentinel.ui.unlock.UnlockScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Full-screen lock shown when the phone wakes up.
 * Uses the same main/duress passwords — duress actions run on unlock.
 */
@AndroidEntryPoint
class DeviceLockActivity : LocaleAwareComponentActivity() {

    @Inject lateinit var deviceLockController: DeviceLockController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        onBackPressedDispatcher.addCallback(this) { /* require password */ }

        enableEdgeToEdge()
        setContent {
            SentinelLocaleProvider {
                SentinelTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        UnlockScreen(
                            isDeviceLock = true,
                            onUnlocked = ::onUnlockSuccess
                        )
                    }
                }
            }
        }
    }

    private fun onUnlockSuccess() {
        lifecycleScope.launch {
            deviceLockController.unlockSession()
            dismissKeyguardIfPossible()
            finish()
        }
    }

    private fun dismissKeyguardIfPossible() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val keyguard = getSystemService(KeyguardManager::class.java)
            keyguard?.requestDismissKeyguard(this, null)
        }
    }

}
