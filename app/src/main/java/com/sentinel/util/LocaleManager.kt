package com.sentinel.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.sentinel.domain.model.AppLanguage

/**
 * Applies per-app locale for English or Persian UI.
 */
object LocaleManager {

    private const val PREFS_NAME = "sentinel_locale_sync"
    private const val KEY_LANGUAGE = "app_language"

    fun apply(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language.code)
        )
    }

    fun apply(code: String) {
        apply(AppLanguage.fromCode(code))
    }

    fun persist(context: Context, language: AppLanguage) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language.code)
            .apply()
        apply(language)
    }

    fun currentLanguage(context: Context): AppLanguage {
        val code = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code)
        return AppLanguage.fromCode(code)
    }

    fun restore(context: Context) {
        apply(currentLanguage(context))
    }
}
