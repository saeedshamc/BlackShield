package com.sentinel.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sentinel.domain.model.LockMethod

@Entity(tableName = "locked_apps")
data class LockedAppEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val lockMethod: LockMethod = LockMethod.PIN,
    val lockImmediately: Boolean = true,
    val lockAfterTimeout: Long = 0L,
    val lockOnScreenOff: Boolean = true,
    val isLocked: Boolean = false,
    val lastUnlockedAt: Long = 0L,
    val profileId: Long? = null
)
