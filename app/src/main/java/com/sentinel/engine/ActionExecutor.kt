package com.sentinel.engine

import com.sentinel.data.local.datastore.PreferencesDataStore
import com.sentinel.data.repository.*
import com.sentinel.domain.model.*
import com.sentinel.util.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Executes defensive actions defined in rules, duress passwords, and panic triggers.
 */
@Singleton
class ActionExecutor @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val lockedAppRepository: LockedAppRepository,
    private val decoyRepository: DecoyRepository,
    private val securityEventRepository: SecurityEventRepository,
    private val preferencesDataStore: PreferencesDataStore
) {
    suspend fun executeActions(
        actions: List<SecurityAction>,
        source: EventSource,
        rule: Rule? = null
    ) {
        actions.forEach { action ->
            executeSingle(action, source, rule)
        }
    }

    private suspend fun executeSingle(
        action: SecurityAction,
        source: EventSource,
        rule: Rule?
    ) {
        when (action.type) {
            ActionType.ACTIVATE_PROFILE -> {
                val profileType = action.parameters["profileType"]
                val profile = if (profileType != null) {
                    profileRepository.getProfileByType(ProfileType.valueOf(profileType))
                } else {
                    action.parameters["profileId"]?.toLongOrNull()?.let {
                        profileRepository.getProfileById(it)
                    }
                }
                profile?.let {
                    profileRepository.activateProfile(it.id)
                    logEvent(EventType.PROFILE_SWITCHED, source, rule, "Activated profile: ${it.name}")
                }
            }
            ActionType.LOCK_APPLICATIONS -> {
                lockedAppRepository.lockAllApps()
                logEvent(EventType.APP_LOCKED, source, rule, "All protected apps locked")
            }
            ActionType.HIDE_APPLICATIONS -> {
                logEvent(EventType.SETTINGS_CHANGED, source, rule, "Hidden apps applied via profile")
            }
            ActionType.ACTIVATE_DECOY_MODE -> {
                preferencesDataStore.setDecoyModeActive(true)
                logEvent(EventType.DECOY_ACTIVATED, source, rule, "Decoy mode activated")
            }
            ActionType.EXECUTE_EMERGENCY_ACTIONS -> {
                profileRepository.getProfileByType(ProfileType.EMERGENCY)?.let {
                    profileRepository.activateProfile(it.id)
                }
                lockedAppRepository.lockAllApps()
                preferencesDataStore.setNotificationsSuppressed(true)
                logEvent(EventType.PANIC_TRIGGERED, source, rule, "Emergency actions executed")
            }
            ActionType.DISABLE_NOTIFICATIONS -> {
                preferencesDataStore.setNotificationsSuppressed(true)
                logEvent(EventType.SETTINGS_CHANGED, source, rule, "Notifications suppressed")
            }
            ActionType.LOG_EVENT -> {
                logEvent(EventType.RULE_EXECUTED, source, rule, action.parameters["message"] ?: "Custom log")
            }
            ActionType.WIPE_DECOY_DATA -> {
                decoyRepository.wipeAll()
                logEvent(EventType.DECOY_DEACTIVATED, source, rule, "Decoy data wiped")
            }
            ActionType.LOCK_DEVICE -> {
                logEvent(EventType.INTRUSION_DETECTED, source, rule, "Device lock requested")
            }
            ActionType.CUSTOM -> {
                logEvent(EventType.RULE_EXECUTED, source, rule, action.parameters["description"] ?: "Custom action")
            }
        }
    }

    private suspend fun logEvent(
        type: EventType,
        source: EventSource,
        rule: Rule?,
        details: String
    ) {
        securityEventRepository.logEvent(
            SecurityEvent(
                eventType = type,
                ruleId = rule?.id,
                ruleName = rule?.name,
                triggerSource = source,
                details = details,
                severity = if (type == EventType.PANIC_TRIGGERED || type == EventType.INTRUSION_DETECTED) {
                    EventSeverity.CRITICAL
                } else {
                    EventSeverity.INFO
                }
            )
        )
    }
}
