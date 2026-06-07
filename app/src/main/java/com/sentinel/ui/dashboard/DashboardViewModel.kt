package com.sentinel.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.data.local.datastore.PreferencesDataStore
import com.sentinel.data.repository.RuleRepository
import com.sentinel.domain.model.DashboardState
import com.sentinel.domain.model.SecurityStatus
import com.sentinel.domain.usecase.profile.ObserveActiveProfileUseCase
import com.sentinel.domain.usecase.security.ObserveSecurityEventsUseCase
import com.sentinel.domain.usecase.applocker.ObserveLockedAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeActiveProfile: ObserveActiveProfileUseCase,
    observeSecurityEvents: ObserveSecurityEventsUseCase,
    observeLockedApps: ObserveLockedAppsUseCase,
    ruleRepository: RuleRepository,
    preferencesDataStore: PreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                observeActiveProfile(),
                ruleRepository.observeEnabledCount(),
                observeSecurityEvents.recent(5),
                preferencesDataStore.decoyModeActive,
                observeLockedApps()
            ) { profile, rulesCount, events, decoyActive, lockedApps ->
                DashboardState(
                    activeProfile = profile,
                    securityStatus = SecurityStatus(
                        encryptionActive = true,
                        overallScore = calculateScore(rulesCount, lockedApps.size)
                    ),
                    activeRulesCount = rulesCount,
                    recentEvents = events,
                    decoyModeActive = decoyActive,
                    lockedAppsCount = lockedApps.size
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    private fun calculateScore(rulesCount: Int, lockedAppsCount: Int): Int {
        var score = 40
        score += (rulesCount * 5).coerceAtMost(30)
        score += (lockedAppsCount * 3).coerceAtMost(30)
        return score.coerceIn(0, 100)
    }
}
