package com.sentinel.security

import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Prevents the app from re-locking when the user briefly leaves to pick files (SAF picker).
 */
@Singleton
class AppLockCoordinator @Inject constructor() {
    private val suppressNextStop = AtomicBoolean(false)

    fun suppressLockOnNextStop() {
        suppressNextStop.set(true)
    }

    fun shouldLockOnStop(): Boolean = !suppressNextStop.getAndSet(false)
}
