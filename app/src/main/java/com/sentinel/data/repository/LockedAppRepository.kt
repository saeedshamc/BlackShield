package com.sentinel.data.repository

import com.sentinel.data.local.dao.DecoyDataDao
import com.sentinel.data.local.dao.LockedAppDao
import com.sentinel.data.local.database.DatabaseCallback
import com.sentinel.data.local.entity.DecoyDataEntity
import com.sentinel.data.mapper.EntityMappers.toDomain
import com.sentinel.data.mapper.EntityMappers.toEntity
import com.sentinel.domain.model.DecoyCategory
import com.sentinel.domain.model.DecoyContent
import com.sentinel.domain.model.LockedApp
import com.sentinel.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockedAppRepository @Inject constructor(
    private val lockedAppDao: LockedAppDao
) {
    fun observeAllLockedApps(): Flow<List<LockedApp>> =
        lockedAppDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeLockedCount(): Flow<Int> = lockedAppDao.observeCount()

    suspend fun getByPackage(packageName: String): LockedApp? =
        lockedAppDao.getByPackage(packageName)?.toDomain()

    suspend fun saveLockedApp(app: LockedApp): Long {
        val entity = app.toEntity()
        return if (entity.id == 0L) lockedAppDao.insert(entity) else {
            lockedAppDao.update(entity)
            entity.id
        }
    }

    suspend fun lockAllApps() = lockedAppDao.lockAll()

    suspend fun lockApp(packageName: String) = lockedAppDao.lockPackage(packageName)

    suspend fun deleteLockedApp(app: LockedApp) = lockedAppDao.delete(app.toEntity())
}

@Singleton
class DecoyRepository @Inject constructor(
    private val decoyDataDao: DecoyDataDao,
    private val cryptoManager: CryptoManager
) {
    private var seeded = false

    fun observeByCategory(category: DecoyCategory): Flow<List<DecoyContent>> =
        decoyDataDao.observeByCategory(category).map { list ->
            list.map { entity ->
                entity.toDomain(cryptoManager.decrypt(entity.contentEncrypted))
            }
        }

    fun observeAll(): Flow<List<DecoyContent>> =
        decoyDataDao.observeAll().map { list ->
            list.map { entity ->
                entity.toDomain(cryptoManager.decrypt(entity.contentEncrypted))
            }
        }

    suspend fun ensureDefaultContent() {
        if (seeded) return
        val existing = decoyDataDao.observeAll().first()
        if (existing.isEmpty()) {
            DatabaseCallback.defaultDecoyContent().forEach { seed ->
                decoyDataDao.insert(
                    DecoyDataEntity(
                        category = seed.category,
                        title = seed.title,
                        contentEncrypted = cryptoManager.encrypt(seed.content)
                    )
                )
            }
        }
        seeded = true
    }

    suspend fun saveContent(content: DecoyContent): Long {
        val entity = DecoyDataEntity(
            id = content.id,
            category = content.category,
            title = content.title,
            contentEncrypted = cryptoManager.encrypt(content.content),
            metadata = content.metadata,
            createdAt = content.createdAt
        )
        return if (entity.id == 0L) decoyDataDao.insert(entity) else {
            decoyDataDao.update(entity)
            entity.id
        }
    }

    suspend fun wipeAll() = decoyDataDao.deleteAll()
}
