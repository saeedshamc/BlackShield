package com.sentinel.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sentinel.domain.model.Condition
import com.sentinel.domain.model.SecurityAction
import com.sentinel.domain.model.Trigger

@Entity(tableName = "rules")
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val enabled: Boolean = true,
    val priority: Int = 0,
    val trigger: Trigger,
    val conditions: List<Condition> = emptyList(),
    val actions: List<SecurityAction> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "triggers")
data class TriggerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleId: Long,
    val triggerJson: String,
    val enabled: Boolean = true
)

@Entity(tableName = "conditions")
data class ConditionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleId: Long,
    val conditionJson: String,
    val sortOrder: Int = 0
)

@Entity(tableName = "actions")
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleId: Long,
    val actionJson: String,
    val sortOrder: Int = 0
)
