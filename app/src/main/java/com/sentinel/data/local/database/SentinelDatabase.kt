package com.sentinel.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sentinel.data.local.converter.SentinelTypeConverters
import com.sentinel.data.local.dao.*
import com.sentinel.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        RuleEntity::class,
        TriggerEntity::class,
        ConditionEntity::class,
        ActionEntity::class,
        ProfileEntity::class,
        LogEntity::class,
        LockedAppEntity::class,
        DecoyDataEntity::class,
        ButtonTriggerEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(SentinelTypeConverters::class)
abstract class SentinelDatabase : RoomDatabase() {
    abstract fun ruleDao(): RuleDao
    abstract fun profileDao(): ProfileDao
    abstract fun logDao(): LogDao
    abstract fun lockedAppDao(): LockedAppDao
    abstract fun decoyDataDao(): DecoyDataDao
    abstract fun buttonTriggerDao(): ButtonTriggerDao
    abstract fun settingsDao(): SettingsDao
}
