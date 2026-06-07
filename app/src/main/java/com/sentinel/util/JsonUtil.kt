package com.sentinel.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtil {
    private val gson = Gson()

    fun toJson(obj: Any?): String = gson.toJson(obj)

    inline fun <reified T> fromJson(json: String): T =
        gson.fromJson(json, object : TypeToken<T>() {}.type)

    inline fun <reified T> fromJsonOrNull(json: String?): T? {
        if (json.isNullOrBlank()) return null
        return runCatching { fromJson<T>(json) }.getOrNull()
    }
}
