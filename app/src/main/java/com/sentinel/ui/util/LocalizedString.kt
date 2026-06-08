package com.sentinel.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sentinel.util.LocaleManager

/**
 * Loads a string from the user-selected app locale.
 * Fallback for Compose when activity resources need explicit localization.
 */
@Composable
fun localizedStringResource(@StringRes id: Int, vararg formatArgs: Any): String {
    val activityContext = LocalContext.current
    return remember(activityContext, id, formatArgs.contentHashCode()) {
        val language = LocaleManager.currentLanguage(activityContext)
        val localized = LocaleManager.localizedContext(activityContext, language)
        if (formatArgs.isEmpty()) {
            localized.getString(id)
        } else {
            localized.getString(id, *formatArgs)
        }
    }
}
