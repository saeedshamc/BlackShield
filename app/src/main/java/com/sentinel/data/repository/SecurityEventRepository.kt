package com.sentinel.data.repository

import com.sentinel.data.local.dao.LogDao
import com.sentinel.data.mapper.EntityMappers.toDomain
import com.sentinel.data.mapper.EntityMappers.toEntity
import com.sentinel.domain.model.SecurityEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityEventRepository @Inject constructor(
    private val logDao: LogDao
) {
    fun observeAllEvents(): Flow<List<SecurityEvent>> =
        logDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeRecentEvents(limit: Int = 10): Flow<List<SecurityEvent>> =
        logDao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    fun searchEvents(query: String): Flow<List<SecurityEvent>> =
        logDao.search(query).map { list -> list.map { it.toDomain() } }

    suspend fun logEvent(event: SecurityEvent): Long =
        logDao.insert(event.toEntity())

    suspend fun purgeOldEvents(retentionDays: Int) {
        if (retentionDays <= 0) {
            logDao.deleteAll()
        } else {
            val cutoff = System.currentTimeMillis() - (retentionDays * 24L * 60 * 60 * 1000)
            logDao.deleteOlderThan(cutoff)
        }
    }

    suspend fun clearAllEvents() = logDao.deleteAll()
}
