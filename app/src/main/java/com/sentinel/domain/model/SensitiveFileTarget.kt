package com.sentinel.domain.model

/**
 * User-configured file or folder to delete on duress unlock or rule execution.
 * Scoped deletion only — never wipes the entire device.
 */
data class SensitiveFileTarget(
    val id: String = java.util.UUID.randomUUID().toString(),
    val displayName: String,
    /** Absolute path or content:// URI from the system file picker. */
    val filePath: String,
    val isFolder: Boolean = false,
    val deleteOnDuress1: Boolean = false,
    val deleteOnDuress2: Boolean = false,
    val deleteOnDuress3: Boolean = false,
    val enabled: Boolean = true
) {
    val isContentUri: Boolean get() = filePath.startsWith("content://")
}

/** Catalog entry for selectable duress/rule actions in the UI. */
data class ActionCatalogItem(
    val type: ActionType,
    val requiresProfile: Boolean = false,
    val isDestructive: Boolean = false
)

object DuressActionCatalog {
    val availableActions: List<ActionCatalogItem> = listOf(
        ActionCatalogItem(ActionType.ACTIVATE_DECOY_MODE),
        ActionCatalogItem(ActionType.LOCK_APPLICATIONS),
        ActionCatalogItem(ActionType.WIPE_DECOY_DATA, isDestructive = true),
        ActionCatalogItem(ActionType.DELETE_SELECTED_FILES, isDestructive = true),
        ActionCatalogItem(ActionType.CLEAR_SECURITY_LOGS, isDestructive = true),
        ActionCatalogItem(ActionType.DISABLE_NOTIFICATIONS),
        ActionCatalogItem(ActionType.ACTIVATE_PROFILE, requiresProfile = true),
        ActionCatalogItem(ActionType.EXECUTE_EMERGENCY_ACTIONS, isDestructive = true)
    )
}
