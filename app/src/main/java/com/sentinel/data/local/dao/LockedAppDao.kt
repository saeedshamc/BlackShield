package com.sentinel.data.local.dao

import androidx.room.*
import com.sentinel.data.local.entity.LockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LockedAppDao {
    @Query("SELECT * FROM locked_apps ORDER BY appName ASC")
    fun observeAll(): Flow<List<LockedAppEntity>>

    @Query("SELECT * FROM locked_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getByPackage(packageName: String): LockedAppEntity?

    @Query("SELECT COUNT(*) FROM locked_apps")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM locked_apps WHERE isLocked = 1")
    fun observeLockedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: LockedAppEntity): Long

    @Update
    suspend fun update(app: LockedAppEntity)

    @Delete
    suspend fun delete(app: LockedAppEntity)

    @Query("UPDATE locked_apps SET isLocked = 1")
    suspend fun lockAll()

    @Query("UPDATE locked_apps SET isLocked = 1 WHERE packageName = :packageName")
    suspend fun lockPackage(packageName: String)
}
