package com.sentinel.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sentinel.domain.model.ButtonCombination
import com.sentinel.domain.model.SecurityAction

@Entity(tableName = "button_triggers")
data class ButtonTriggerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val combination: ButtonCombination,
    val enabled: Boolean = true,
    val ruleId: Long? = null,
    val actions: List<SecurityAction> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val key: String,
    val valueEncrypted: String,
    val updatedAt: Long = System.currentTimeMillis()
)
