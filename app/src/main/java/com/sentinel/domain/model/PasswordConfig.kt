package com.sentinel.domain.model

/**
 * Duress password configuration (Module 3).
 * Multiple passwords map to different defensive actions.
 */
data class PasswordConfig(
    val mainPasswordHash: String = "",
    val duressPassword1Hash: String = "",
    val duressPassword2Hash: String = "",
    val duressPassword3Hash: String = "",
    val duress1Actions: List<SecurityAction> = listOf(
        SecurityAction(ActionType.ACTIVATE_DECOY_MODE)
    ),
    val duress2Actions: List<SecurityAction> = listOf(
        SecurityAction(ActionType.LOCK_APPLICATIONS)
    ),
    val duress3Actions: List<SecurityAction> = listOf(
        SecurityAction(ActionType.EXECUTE_EMERGENCY_ACTIONS),
        SecurityAction(ActionType.ACTIVATE_PROFILE, mapOf("profileType" to "EMERGENCY"))
    ),
    val biometricEnabled: Boolean = false,
    val maxFailedAttempts: Int = 5
)

enum class PasswordType {
    MAIN,
    DURESS_1,
    DURESS_2,
    DURESS_3,
    INVALID
}

data class PasswordVerificationResult(
    val type: PasswordType,
    val actions: List<SecurityAction> = emptyList()
)
