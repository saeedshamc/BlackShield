package com.sentinel.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.sentinel.domain.model.AppLanguage
import com.sentinel.util.LocaleManager

/**
 * Applies RTL/LTR layout direction for Persian/English.
 * Strings come from the locale-wrapped activity context (attachBaseContext).
 * Do NOT override LocalContext here — it breaks Hilt and Activity lookups.
 */
@Composable
fun SentinelLocaleProvider(content: @Composable () -> Unit) {
    val language = LocaleManager.currentLanguage(LocalContext.current)
    val layoutDirection = if (language == AppLanguage.PERSIAN) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        content()
    }
}
