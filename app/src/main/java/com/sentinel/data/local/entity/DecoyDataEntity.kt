package com.sentinel.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sentinel.domain.model.DecoyCategory

@Entity(tableName = "decoy_data")
data class DecoyDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: DecoyCategory,
    val title: String,
    val contentEncrypted: String,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)
