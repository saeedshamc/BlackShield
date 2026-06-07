package com.sentinel.domain.usecase.rule

import com.sentinel.data.repository.RuleRepository
import com.sentinel.domain.model.Rule
import com.sentinel.engine.RuleEngine
import com.sentinel.domain.model.SecurityEventInput
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRulesUseCase @Inject constructor(private val repository: RuleRepository) {
    operator fun invoke(): Flow<List<Rule>> = repository.observeAllRules()
}

class SaveRuleUseCase @Inject constructor(private val repository: RuleRepository) {
    suspend operator fun invoke(rule: Rule): Long = repository.saveRule(rule)
}

class DeleteRuleUseCase @Inject constructor(private val repository: RuleRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteRule(id)
}

class ProcessSecurityEventUseCase @Inject constructor(private val ruleEngine: RuleEngine) {
    suspend operator fun invoke(event: SecurityEventInput) = ruleEngine.processEvent(event)
}

class ToggleRuleUseCase @Inject constructor(private val repository: RuleRepository) {
    suspend operator fun invoke(id: Long, enabled: Boolean) = repository.toggleRule(id, enabled)
}
