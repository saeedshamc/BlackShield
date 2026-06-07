package com.sentinel.data.local.dao

import androidx.room.*
import com.sentinel.data.local.entity.DecoyDataEntity
import com.sentinel.domain.model.DecoyCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface DecoyDataDao {
    @Query("SELECT * FROM decoy_data WHERE category = :category ORDER BY createdAt DESC")
    fun observeByCategory(category: DecoyCategory): Flow<List<DecoyDataEntity>>

    @Query("SELECT * FROM decoy_data ORDER BY category, createdAt DESC")
    fun observeAll(): Flow<List<DecoyDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DecoyDataEntity): Long

    @Update
    suspend fun update(data: DecoyDataEntity)

    @Delete
    suspend fun delete(data: DecoyDataEntity)

    @Query("DELETE FROM decoy_data")
    suspend fun deleteAll()
}
