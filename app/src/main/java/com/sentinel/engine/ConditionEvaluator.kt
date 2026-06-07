package com.sentinel.engine

import com.sentinel.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Evaluates rule conditions against current system state.
 */
@Singleton
class ConditionEvaluator @Inject constructor() {

    fun evaluate(
        conditions: List<Condition>,
        context: ConditionContext
    ): Boolean {
        if (conditions.isEmpty()) return true

        val andConditions = conditions.filter { it.operator == ConditionOperator.AND }
        val orConditions = conditions.filter { it.operator == ConditionOperator.OR }

        val andResult = andConditions.isEmpty() || andConditions.all { evaluateSingle(it, context) }
        val orResult = orConditions.isEmpty() || orConditions.any { evaluateSingle(it, context) }

        return andResult && orResult
    }

    private fun evaluateSingle(condition: Condition, context: ConditionContext): Boolean {
        return when (condition.type) {
            ConditionType.THRESHOLD_EXCEEDED -> {
                val threshold = condition.intParam("threshold", 1)
                context.numericValue >= threshold
            }
            ConditionType.APP_IS_RUNNING -> {
                context.runningPackage == condition.parameters["packageName"]
            }
            ConditionType.PROFILE_IS_ACTIVE -> {
                context.activeProfileType?.name == condition.parameters["profileType"]
            }
            ConditionType.TIME_IN_RANGE -> {
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                val start = condition.intParam("startHour", 0)
                val end = condition.intParam("endHour", 23)
                hour in start..end
            }
            ConditionType.SIM_PRESENT -> context.simPresent
            ConditionType.BIOMETRIC_AVAILABLE -> context.biometricAvailable
            ConditionType.CUSTOM -> condition.parameters["expression"] == context.customExpression
        }
    }
}

data class ConditionContext(
    val numericValue: Int = 0,
    val runningPackage: String? = null,
    val activeProfileType: ProfileType? = null,
    val simPresent: Boolean = true,
    val biometricAvailable: Boolean = false,
    val customExpression: String? = null
)
