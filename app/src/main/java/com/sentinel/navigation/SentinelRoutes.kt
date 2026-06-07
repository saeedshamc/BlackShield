package com.sentinel.navigation

object SentinelRoutes {
    const val DASHBOARD = "dashboard"
    const val RULES = "rules"
    const val RULE_BUILDER = "rules/builder"
    const val RULE_BUILDER_WITH_ID = "rules/builder/{ruleId}"
    const val PROFILES = "profiles"
    const val PROFILE_EDIT = "profiles/edit/{profileId}"
    const val DECOY = "decoy"
    const val DECOY_GALLERY = "decoy/gallery"
    const val DECOY_CONTACTS = "decoy/contacts"
    const val DECOY_NOTES = "decoy/notes"
    const val APP_LOCKER = "applocker"
    const val PANIC = "panic"
    const val EVENTS = "events"
    const val SETTINGS = "settings"
    const val DURESS_PASSWORD = "settings/duress"
    const val BACKUP = "settings/backup"

    fun ruleBuilder(ruleId: Long? = null): String =
        if (ruleId != null) "rules/builder/$ruleId" else RULE_BUILDER

    fun profileEdit(profileId: Long) = "profiles/edit/$profileId"
}
