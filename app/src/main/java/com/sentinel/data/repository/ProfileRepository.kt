package com.sentinel.data.repository

import com.sentinel.data.local.dao.ProfileDao
import com.sentinel.data.local.database.DatabaseCallback
import com.sentinel.data.local.datastore.PreferencesDataStore
import com.sentinel.data.mapper.EntityMappers.toDomain
import com.sentinel.data.mapper.EntityMappers.toEntity
import com.sentinel.domain.model.Profile
import com.sentinel.domain.model.ProfileType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao,
    private val preferencesDataStore: PreferencesDataStore
) {
    private var seeded = false

    fun observeAllProfiles(): Flow<List<Profile>> =
        profileDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeActiveProfile(): Flow<Profile?> =
        profileDao.observeActive().map { it?.toDomain() }

    suspend fun ensureDefaultProfiles() {
        if (seeded) return
        val existing = profileDao.observeAll().first()
        if (existing.isEmpty()) {
            DatabaseCallback.defaultProfiles().forEach { profileDao.insert(it) }
        }
        seeded = true
    }

    suspend fun getProfileById(id: Long): Profile? = profileDao.getById(id)?.toDomain()

    suspend fun getProfileByType(type: ProfileType): Profile? =
        profileDao.getByType(type.name)?.toDomain()

    suspend fun saveProfile(profile: Profile): Long {
        val entity = profile.toEntity()
        return if (entity.id == 0L) profileDao.insert(entity) else {
            profileDao.update(entity)
            entity.id
        }
    }

    /** Instantly switches active profile and persists selection. */
    suspend fun activateProfile(profileId: Long) {
        profileDao.deactivateAll()
        profileDao.activate(profileId)
        preferencesDataStore.setActiveProfileId(profileId)
    }

    suspend fun deleteProfile(profile: Profile) {
        if (!profile.isDefault) {
            profileDao.delete(profile.toEntity())
        }
    }
}
