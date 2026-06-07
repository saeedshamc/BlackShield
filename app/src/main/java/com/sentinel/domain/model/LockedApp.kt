package com.sentinel.domain.model

/**
 * Application locker models (Module 5).
 */
data class LockedApp(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val lockMethod: LockMethod = LockMethod.PIN,
    val lockImmediately: Boolean = true,
    val lockAfterTimeout: Long = 0L,
    val lockOnScreenOff: Boolean = true,
    val isLocked: Boolean = false,
    val lastUnlockedAt: Long = 0L,
    val profileId: Long? = null
)

enum class LockMethod {
    PIN,
    PASSWORD,
    BIOMETRIC,
    BIOMETRIC_WITH_PIN
}

enum class AppLockPolicy {
    IMMEDIATE,
    TIMEOUT,
    SCREEN_OFF
}
