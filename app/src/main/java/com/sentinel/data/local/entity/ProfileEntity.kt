package com.sentinel.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sentinel.domain.model.DecoySettings
import com.sentinel.domain.model.NotificationBehavior
import com.sentinel.domain.model.ProfileType

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: ProfileType,
    val isDefault: Boolean = false,
    val isActive: Boolean = false,
    val hiddenApps: List<String> = emptyList(),
    val lockedApps: List<String> = emptyList(),
    val notificationBehavior: NotificationBehavior = NotificationBehavior.NORMAL,
    val securityRuleIds: List<Long> = emptyList(),
    val decoySettings: DecoySettings = DecoySettings(),
    val color: String = "#00E5FF",
    val icon: String = "shield",
    val createdAt: Long = System.currentTimeMillis()
)
