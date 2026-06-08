package com.sentinel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sentinel.data.repository.PasswordRepository
import com.sentinel.domain.usecase.security.LockAppUseCase
import com.sentinel.security.AppLockCoordinator
import com.sentinel.navigation.SentinelNavHost
import com.sentinel.ui.base.LocaleAwareComponentActivity
import com.sentinel.ui.gate.AppGateViewModel
import com.sentinel.ui.theme.SentinelLocaleProvider
import com.sentinel.ui.theme.SentinelTheme
import com.sentinel.ui.unlock.UnlockScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : LocaleAwareComponentActivity() {

    @Inject lateinit var lockApp: LockAppUseCase
    @Inject lateinit var passwordRepository: PasswordRepository
    @Inject lateinit var appLockCoordinator: AppLockCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SentinelLocaleProvider {
                SentinelTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val gateViewModel: AppGateViewModel = hiltViewModel()
                        val needsUnlock by gateViewModel.needsUnlock.collectAsStateWithLifecycle()

                        if (needsUnlock && gateViewModel.hasPassword) {
                            UnlockScreen(onUnlocked = { gateViewModel.refresh() })
                        } else {
                            SentinelNavHost()
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (appLockCoordinator.shouldLockOnStop() && passwordRepository.isAnyPasswordSet()) {
            lifecycleScope.launch { lockApp() }
        }
    }
}
