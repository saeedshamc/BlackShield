package com.sentinel.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sentinel.domain.model.EventSeverity
import com.sentinel.domain.model.EventSource
import com.sentinel.domain.model.EventType

@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: EventType,
    val ruleId: Long? = null,
    val ruleName: String? = null,
    val triggerSource: EventSource,
    val details: String = "",
    val severity: EventSeverity = EventSeverity.INFO
)
