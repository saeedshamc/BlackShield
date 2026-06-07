package com.sentinel.data.mapper

import com.sentinel.data.local.entity.*
import com.sentinel.domain.model.*

object EntityMappers {

    fun RuleEntity.toDomain() = Rule(
        id = id,
        name = name,
        description = description,
        enabled = enabled,
        priority = priority,
        trigger = trigger,
        conditions = conditions,
        actions = actions,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun Rule.toEntity() = RuleEntity(
        id = id,
        name = name,
        description = description,
        enabled = enabled,
        priority = priority,
        trigger = trigger,
        conditions = conditions,
        actions = actions,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun ProfileEntity.toDomain() = Profile(
        id = id,
        name = name,
        type = type,
        isDefault = isDefault,
        isActive = isActive,
        hiddenApps = hiddenApps,
        lockedApps = lockedApps,
        notificationBehavior = notificationBehavior,
        securityRules = securityRuleIds,
        decoySettings = decoySettings,
        color = color,
        icon = icon,
        createdAt = createdAt
    )

    fun Profile.toEntity() = ProfileEntity(
        id = id,
        name = name,
        type = type,
        isDefault = isDefault,
        isActive = isActive,
        hiddenApps = hiddenApps,
        lockedApps = lockedApps,
        notificationBehavior = notificationBehavior,
        securityRuleIds = securityRules,
        decoySettings = decoySettings,
        color = color,
        icon = icon,
        createdAt = createdAt
    )

    fun LogEntity.toDomain() = SecurityEvent(
        id = id,
        timestamp = timestamp,
        eventType = eventType,
        ruleId = ruleId,
        ruleName = ruleName,
        triggerSource = triggerSource,
        details = details,
        severity = severity
    )

    fun SecurityEvent.toEntity() = LogEntity(
        id = id,
        timestamp = timestamp,
        eventType = eventType,
        ruleId = ruleId,
        ruleName = ruleName,
        triggerSource = triggerSource,
        details = details,
        severity = severity
    )

    fun LockedAppEntity.toDomain() = LockedApp(
        id = id,
        packageName = packageName,
        appName = appName,
        lockMethod = lockMethod,
        lockImmediately = lockImmediately,
        lockAfterTimeout = lockAfterTimeout,
        lockOnScreenOff = lockOnScreenOff,
        isLocked = isLocked,
        lastUnlockedAt = lastUnlockedAt,
        profileId = profileId
    )

    fun LockedApp.toEntity() = LockedAppEntity(
        id = id,
        packageName = packageName,
        appName = appName,
        lockMethod = lockMethod,
        lockImmediately = lockImmediately,
        lockAfterTimeout = lockAfterTimeout,
        lockOnScreenOff = lockOnScreenOff,
        isLocked = isLocked,
        lastUnlockedAt = lastUnlockedAt,
        profileId = profileId
    )

    fun DecoyDataEntity.toDomain(decryptedContent: String) = DecoyContent(
        id = id,
        category = category,
        title = title,
        content = decryptedContent,
        metadata = metadata,
        createdAt = createdAt
    )

    fun ButtonTriggerEntity.toDomain() = ButtonTrigger(
        id = id,
        name = name,
        combination = combination,
        enabled = enabled,
        ruleId = ruleId,
        actions = actions,
        createdAt = createdAt
    )

    fun ButtonTrigger.toEntity() = ButtonTriggerEntity(
        id = id,
        name = name,
        combination = combination,
        enabled = enabled,
        ruleId = ruleId,
        actions = actions,
        createdAt = createdAt
    )
}
