package com.sentinel.ui.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinel.data.repository.RuleRepository
import com.sentinel.domain.model.*
import com.sentinel.domain.usecase.rule.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RuleListViewModel @Inject constructor(
    observeRules: ObserveRulesUseCase,
    private val deleteRule: DeleteRuleUseCase,
    private val toggleRule: ToggleRuleUseCase
) : ViewModel() {

    val rules: StateFlow<List<Rule>> = observeRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteRule(id: Long) = viewModelScope.launch { deleteRule(id) }
    fun toggleRule(id: Long, enabled: Boolean) = viewModelScope.launch { toggleRule(id, enabled) }
}

@HiltViewModel
class RuleBuilderViewModel @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val saveRule: SaveRuleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RuleBuilderState())
    val uiState: StateFlow<RuleBuilderState> = _uiState.asStateFlow()

    fun loadRule(id: Long?) {
        if (id == null) return
        viewModelScope.launch {
            ruleRepository.getRuleById(id)?.let { rule ->
                _uiState.value = RuleBuilderState(
                    id = rule.id,
                    name = rule.name,
                    description = rule.description,
                    triggerType = rule.trigger.type,
                    triggerThreshold = rule.trigger.intParam("threshold", 5),
                    actionType = rule.actions.firstOrNull()?.type ?: ActionType.LOG_EVENT,
                    profileType = rule.actions.firstOrNull()?.parameters["profileType"] ?: "EMERGENCY",
                    enabled = rule.enabled,
                    priority = rule.priority
                )
            }
        }
    }

    fun updateName(name: String) { _uiState.update { it.copy(name = name) } }
    fun updateDescription(desc: String) { _uiState.update { it.copy(description = desc) } }
    fun updateTriggerType(type: TriggerType) { _uiState.update { it.copy(triggerType = type) } }
    fun updateActionType(type: ActionType) { _uiState.update { it.copy(actionType = type) } }
    fun updateThreshold(value: Int) { _uiState.update { it.copy(triggerThreshold = value) } }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.name.isBlank()) return@launch

            val rule = Rule(
                id = state.id,
                name = state.name,
                description = state.description,
                enabled = state.enabled,
                priority = state.priority,
                trigger = buildTrigger(state),
                conditions = emptyList(),
                actions = listOf(buildAction(state))
            )
            saveRule(rule)
            onSaved()
        }
    }

    private fun buildTrigger(state: RuleBuilderState): Trigger = when (state.triggerType) {
        TriggerType.WRONG_PASSWORD_ATTEMPTS, TriggerType.UNLOCK_FAILURE ->
            Trigger(state.triggerType, mapOf("threshold" to state.triggerThreshold.toString()))
        TriggerType.POWER_BUTTON_PRESS ->
            Trigger(state.triggerType, mapOf("count" to state.triggerThreshold.toString()))
        else -> Trigger(state.triggerType)
    }

    private fun buildAction(state: RuleBuilderState): SecurityAction = when (state.actionType) {
        ActionType.ACTIVATE_PROFILE ->
            SecurityAction(state.actionType, mapOf("profileType" to state.profileType))
        else -> SecurityAction(state.actionType)
    }
}

data class RuleBuilderState(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val triggerType: TriggerType = TriggerType.WRONG_PASSWORD_ATTEMPTS,
    val triggerThreshold: Int = 5,
    val actionType: ActionType = ActionType.LOCK_APPLICATIONS,
    val profileType: String = "EMERGENCY",
    val enabled: Boolean = true,
    val priority: Int = 0
)
