package com.sentinel.security

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Prevents the app from re-locking when the user briefly leaves to pick files (SAF picker).
 */
@Singleton
class AppLockCoordinator @Inject constructor() {
    private val suppressStopsRemaining = java.util.concurrent.atomic.AtomicInteger(0)

    /** Suppress re-lock while user leaves app for picker (open + return). */
    fun suppressLockForExternalPicker() {
        suppressStopsRemaining.set(2)
    }

    fun shouldLockOnStop(): Boolean {
        val remaining = suppressStopsRemaining.get()
        return if (remaining > 0) {
            suppressStopsRemaining.decrementAndGet()
            false
        } else {
            true
        }
    }
}
