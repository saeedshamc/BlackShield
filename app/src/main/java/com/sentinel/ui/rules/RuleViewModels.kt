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
    private val deleteRuleUseCase: DeleteRuleUseCase,
    private val toggleRuleUseCase: ToggleRuleUseCase
) : ViewModel() {

    val rules: StateFlow<List<Rule>> = observeRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteRule(id: Long) = viewModelScope.launch { deleteRuleUseCase(id) }
    fun toggleRule(id: Long, enabled: Boolean) = viewModelScope.launch { toggleRuleUseCase(id, enabled) }
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
                    triggerThreshold = rule.trigger.intParam("threshold", rule.trigger.intParam("count", 5)),
                    actions = rule.actions.map { action ->
                        RuleActionItem(
                            type = action.type,
                            profileType = action.parameters["profileType"] ?: "EMERGENCY"
                        )
                    }.ifEmpty { listOf(RuleActionItem(ActionType.LOG_EVENT)) },
                    enabled = rule.enabled,
                    priority = rule.priority
                )
            }
        }
    }

    fun updateName(name: String) { _uiState.update { it.copy(name = name) } }
    fun updateDescription(desc: String) { _uiState.update { it.copy(description = desc) } }
    fun updateTriggerType(type: TriggerType) { _uiState.update { it.copy(triggerType = type) } }
    fun updateThreshold(value: Int) { _uiState.update { it.copy(triggerThreshold = value) } }

    fun addAction(type: ActionType) {
        _uiState.update { state ->
            if (state.actions.any { it.type == type }) state
            else state.copy(actions = state.actions + RuleActionItem(type))
        }
    }

    fun removeAction(index: Int) {
        _uiState.update { state ->
            if (state.actions.size <= 1) state
            else state.copy(actions = state.actions.filterIndexed { i, _ -> i != index })
        }
    }

    fun updateActionProfile(index: Int, profile: String) {
        _uiState.update { state ->
            state.copy(actions = state.actions.mapIndexed { i, item ->
                if (i == index) item.copy(profileType = profile) else item
            })
        }
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.name.isBlank() || state.actions.isEmpty()) return@launch

            val rule = Rule(
                id = state.id,
                name = state.name,
                description = state.description,
                enabled = state.enabled,
                priority = state.priority,
                trigger = buildTrigger(state),
                conditions = emptyList(),
                actions = state.actions.map { buildAction(it) }
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

    private fun buildAction(item: RuleActionItem): SecurityAction = when (item.type) {
        ActionType.ACTIVATE_PROFILE ->
            SecurityAction(item.type, mapOf("profileType" to item.profileType))
        else -> SecurityAction(item.type)
    }

    companion object {
        val thresholdTriggers = setOf(
            TriggerType.WRONG_PASSWORD_ATTEMPTS,
            TriggerType.UNLOCK_FAILURE,
            TriggerType.POWER_BUTTON_PRESS
        )
    }
}

data class RuleActionItem(
    val type: ActionType,
    val profileType: String = "EMERGENCY"
)

data class RuleBuilderState(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val triggerType: TriggerType = TriggerType.WRONG_PASSWORD_ATTEMPTS,
    val triggerThreshold: Int = 5,
    val actions: List<RuleActionItem> = listOf(RuleActionItem(ActionType.LOCK_APPLICATIONS)),
    val enabled: Boolean = true,
    val priority: Int = 0
)
