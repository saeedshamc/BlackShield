package com.sentinel.domain.usecase.applocker

import com.sentinel.data.repository.LockedAppRepository
import com.sentinel.domain.model.LockedApp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLockedAppsUseCase @Inject constructor(private val repository: LockedAppRepository) {
    operator fun invoke(): Flow<List<LockedApp>> = repository.observeAllLockedApps()
}

class SaveLockedAppUseCase @Inject constructor(private val repository: LockedAppRepository) {
    suspend operator fun invoke(app: LockedApp): Long = repository.saveLockedApp(app)
}

class LockAllAppsUseCase @Inject constructor(private val repository: LockedAppRepository) {
    suspend operator fun invoke() = repository.lockAllApps()
}

class RemoveLockedAppUseCase @Inject constructor(private val repository: LockedAppRepository) {
    suspend operator fun invoke(app: LockedApp) = repository.deleteLockedApp(app)
}
