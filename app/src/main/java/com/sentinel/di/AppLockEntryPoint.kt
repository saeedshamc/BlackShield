package com.sentinel.di

import com.sentinel.security.AppLockCoordinator
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppLockEntryPoint {
    fun appLockCoordinator(): AppLockCoordinator
}
