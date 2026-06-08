package com.sentinel.util

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.sentinel.domain.model.AppLanguage
import java.util.Locale

/**
 * Applies per-app locale for English or Persian UI.
 */
object LocaleManager {

    private const val TAG = "LocaleManager"
    private const val PREFS_NAME = "sentinel_locale_sync"
    private const val KEY_LANGUAGE = "app_language"

    fun apply(language: AppLanguage) {
        runCatching {
            val locale = localeFor(language)
            Locale.setDefault(locale)
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.create(locale)
            )
        }.onFailure { Log.e(TAG, "Failed to apply locale", it) }
    }

    fun apply(code: String) {
        apply(AppLanguage.fromCode(code))
    }

    fun persist(context: Context, language: AppLanguage) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language.code)
            .commit()
        apply(language)
    }

    fun currentLanguage(context: Context): AppLanguage {
        return runCatching {
            val code = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code)
            AppLanguage.fromCode(code)
        }.getOrDefault(AppLanguage.ENGLISH)
    }

    fun restore(context: Context) {
        apply(currentLanguage(context))
    }

    fun wrap(context: Context): Context {
        return runCatching {
            localizedContext(context, currentLanguage(context))
        }.getOrElse {
            Log.e(TAG, "Failed to wrap context, using default", it)
            context
        }
    }

    fun localizedContext(context: Context, language: AppLanguage): Context {
        val locale = localeFor(language)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLocales(LocaleList(locale))
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    private fun localeFor(language: AppLanguage): Locale = when (language) {
        AppLanguage.PERSIAN -> Locale.forLanguageTag("fa")
        AppLanguage.ENGLISH -> Locale.ENGLISH
    }
}
