package com.sentinel.data.local.dao

import androidx.room.*
import com.sentinel.data.local.entity.ButtonTriggerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ButtonTriggerDao {
    @Query("SELECT * FROM button_triggers WHERE enabled = 1")
    fun observeEnabled(): Flow<List<ButtonTriggerEntity>>

    @Query("SELECT * FROM button_triggers ORDER BY name ASC")
    fun observeAll(): Flow<List<ButtonTriggerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trigger: ButtonTriggerEntity): Long

    @Update
    suspend fun update(trigger: ButtonTriggerEntity)

    @Delete
    suspend fun delete(trigger: ButtonTriggerEntity)
}
