package com.sentinel.data.local.converter

import androidx.room.TypeConverter
import com.sentinel.domain.model.*
import com.sentinel.util.JsonUtil

class SentinelTypeConverters {

    @TypeConverter
    fun fromTrigger(trigger: Trigger): String = JsonUtil.toJson(trigger)

    @TypeConverter
    fun toTrigger(json: String): Trigger =
        JsonUtil.fromJsonOrNull(json) ?: Trigger(TriggerType.MANUAL)

    @TypeConverter
    fun fromConditionList(conditions: List<Condition>): String = JsonUtil.toJson(conditions)

    @TypeConverter
    fun toConditionList(json: String): List<Condition> =
        JsonUtil.fromJsonOrNull(json) ?: emptyList()

    @TypeConverter
    fun fromActionList(actions: List<SecurityAction>): String = JsonUtil.toJson(actions)

    @TypeConverter
    fun toActionList(json: String): List<SecurityAction> =
        JsonUtil.fromJsonOrNull(json) ?: emptyList()

    @TypeConverter
    fun fromStringList(list: List<String>): String = JsonUtil.toJson(list)

    @TypeConverter
    fun toStringList(json: String): List<String> =
        JsonUtil.fromJsonOrNull(json) ?: emptyList()

    @TypeConverter
    fun fromLongList(list: List<Long>): String = JsonUtil.toJson(list)

    @TypeConverter
    fun toLongList(json: String): List<Long> =
        JsonUtil.fromJsonOrNull(json) ?: emptyList()

    @TypeConverter
    fun fromDecoySettings(settings: DecoySettings): String = JsonUtil.toJson(settings)

    @TypeConverter
    fun toDecoySettings(json: String): DecoySettings =
        JsonUtil.fromJsonOrNull(json) ?: DecoySettings()

    @TypeConverter
    fun fromProfileType(type: ProfileType): String = type.name

    @TypeConverter
    fun toProfileType(name: String): ProfileType =
        runCatching { ProfileType.valueOf(name) }.getOrDefault(ProfileType.CUSTOM)

    @TypeConverter
    fun fromNotificationBehavior(behavior: NotificationBehavior): String = behavior.name

    @TypeConverter
    fun toNotificationBehavior(name: String): NotificationBehavior =
        runCatching { NotificationBehavior.valueOf(name) }.getOrDefault(NotificationBehavior.NORMAL)

    @TypeConverter
    fun fromLockMethod(method: LockMethod): String = method.name

    @TypeConverter
    fun toLockMethod(name: String): LockMethod =
        runCatching { LockMethod.valueOf(name) }.getOrDefault(LockMethod.PIN)

    @TypeConverter
    fun fromDecoyCategory(category: DecoyCategory): String = category.name

    @TypeConverter
    fun toDecoyCategory(name: String): DecoyCategory =
        runCatching { DecoyCategory.valueOf(name) }.getOrDefault(DecoyCategory.NOTES)

    @TypeConverter
    fun fromEventType(type: EventType): String = type.name

    @TypeConverter
    fun toEventType(name: String): EventType =
        runCatching { EventType.valueOf(name) }.getOrDefault(EventType.RULE_EXECUTED)

    @TypeConverter
    fun fromEventSource(source: EventSource): String = source.name

    @TypeConverter
    fun toEventSource(name: String): EventSource =
        runCatching { EventSource.valueOf(name) }.getOrDefault(EventSource.MANUAL)

    @TypeConverter
    fun fromEventSeverity(severity: EventSeverity): String = severity.name

    @TypeConverter
    fun toEventSeverity(name: String): EventSeverity =
        runCatching { EventSeverity.valueOf(name) }.getOrDefault(EventSeverity.INFO)

    @TypeConverter
    fun fromButtonCombination(combo: ButtonCombination): String = combo.name

    @TypeConverter
    fun toButtonCombination(name: String): ButtonCombination =
        runCatching { ButtonCombination.valueOf(name) }.getOrDefault(ButtonCombination.POWER_X5)

    @TypeConverter
    fun fromMetadataMap(map: Map<String, String>): String = JsonUtil.toJson(map)

    @TypeConverter
    fun toMetadataMap(json: String): Map<String, String> =
        JsonUtil.fromJsonOrNull(json) ?: emptyMap()
}
