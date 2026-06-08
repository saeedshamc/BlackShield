package com.sentinel.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.sentinel.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * EncryptedSharedPreferences wrapper for sensitive key-value storage.
 */
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            Constants.PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getString(key: String, default: String = ""): String =
        prefs.getString(key, default) ?: default

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    /** Synchronous write — required for passwords to survive immediate power-off/reboot. */
    fun putStringSync(key: String, value: String): Boolean =
        prefs.edit().putString(key, value).commit()

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getLong(key: String, default: Long = 0L): Long =
        prefs.getLong(key, default)

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
