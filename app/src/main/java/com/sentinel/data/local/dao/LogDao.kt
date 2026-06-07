package com.sentinel.data.local.dao

import androidx.room.*
import com.sentinel.data.local.entity.LogEntity
import com.sentinel.domain.model.EventType
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM logs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs WHERE eventType = :type ORDER BY timestamp DESC")
    fun observeByType(type: EventType): Flow<List<LogEntity>>

    @Query("""
        SELECT * FROM logs 
        WHERE details LIKE '%' || :query || '%' 
           OR ruleName LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun search(query: String): Flow<List<LogEntity>>

    @Insert
    suspend fun insert(log: LogEntity): Long

    @Query("DELETE FROM logs WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("SELECT COUNT(*) FROM logs")
    fun observeCount(): Flow<Int>
}
