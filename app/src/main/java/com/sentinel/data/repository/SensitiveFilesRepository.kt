package com.sentinel.data.repository

import com.sentinel.domain.model.SensitiveFileTarget
import com.sentinel.security.SecurePreferences
import com.sentinel.util.JsonUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensitiveFilesRepository @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    private companion object {
        const val KEY_TARGETS = "sensitive_file_targets"
    }

    fun getAll(): List<SensitiveFileTarget> =
        JsonUtil.fromJsonOrNull<List<SensitiveFileTarget>>(securePreferences.getString(KEY_TARGETS))
            ?: emptyList()

    fun saveAll(targets: List<SensitiveFileTarget>) {
        securePreferences.putString(KEY_TARGETS, JsonUtil.toJson(targets))
    }

    fun add(target: SensitiveFileTarget) {
        saveAll(getAll() + target)
    }

    fun update(target: SensitiveFileTarget) {
        saveAll(getAll().map { if (it.id == target.id) target else it })
    }

    fun remove(id: String) {
        saveAll(getAll().filter { it.id != id })
    }
}
