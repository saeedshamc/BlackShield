package com.sentinel.data.local.dao

import androidx.room.*
import com.sentinel.data.local.entity.SettingsEntity

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE `key` = :key")
    suspend fun get(key: String): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: SettingsEntity)

    @Query("DELETE FROM settings WHERE `key` = :key")
    suspend fun delete(key: String)
}
