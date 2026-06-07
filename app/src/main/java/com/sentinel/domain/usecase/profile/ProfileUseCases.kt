package com.sentinel.domain.usecase.profile

import com.sentinel.data.repository.ProfileRepository
import com.sentinel.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProfilesUseCase @Inject constructor(private val repository: ProfileRepository) {
    operator fun invoke(): Flow<List<Profile>> = repository.observeAllProfiles()
}

class ObserveActiveProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    operator fun invoke(): Flow<Profile?> = repository.observeActiveProfile()
}

class ActivateProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profileId: Long) = repository.activateProfile(profileId)
}

class SaveProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile): Long = repository.saveProfile(profile)
}

class EnsureDefaultProfilesUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke() = repository.ensureDefaultProfiles()
}
