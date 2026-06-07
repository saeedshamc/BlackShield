package com.sentinel.engine

import com.sentinel.data.repository.RuleRepository
import com.sentinel.data.repository.SecurityEventRepository
import com.sentinel.domain.model.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core automation engine (Module 1).
 * Processes: Trigger → Condition → Action
 */
@Singleton
class RuleEngine @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val triggerEvaluator: TriggerEvaluator,
    private val conditionEvaluator: ConditionEvaluator,
    private val actionExecutor: ActionExecutor,
    private val securityEventRepository: SecurityEventRepository
) {
    suspend fun processEvent(event: SecurityEventInput) {
        val rules = ruleRepository.observeEnabledRules().first()
        val matchingRules = rules.filter { rule ->
            triggerEvaluator.matches(rule, event)
        }.sortedByDescending { it.priority }

        for (rule in matchingRules) {
            val context = buildContext(event)
            if (conditionEvaluator.evaluate(rule.conditions, context)) {
                actionExecutor.executeActions(rule.actions, event.source, rule)
                securityEventRepository.logEvent(
                    SecurityEvent(
                        eventType = EventType.RULE_EXECUTED,
                        ruleId = rule.id,
                        ruleName = rule.name,
                        triggerSource = event.source,
                        details = "Rule '${rule.name}' executed via ${event.triggerType.name}",
                        severity = EventSeverity.INFO
                    )
                )
            }
        }
    }

    suspend fun executeManualRule(ruleId: Long) {
        val rule = ruleRepository.getRuleById(ruleId) ?: return
        actionExecutor.executeActions(rule.actions, EventSource.MANUAL, rule)
    }

    private fun buildContext(event: SecurityEventInput): ConditionContext {
        return ConditionContext(
            numericValue = event.payload["attemptCount"]?.toIntOrNull() ?: 0,
            runningPackage = event.payload["packageName"],
            activeProfileType = event.payload["activeProfileType"]?.let {
                runCatching { ProfileType.valueOf(it) }.getOrNull()
            },
            simPresent = event.payload["simPresent"]?.toBoolean() ?: true,
            customExpression = event.payload["expression"]
        )
    }
}
