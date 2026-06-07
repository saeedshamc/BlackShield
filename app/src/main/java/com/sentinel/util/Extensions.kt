package com.sentinel.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T, R> Flow<List<T>>.mapList(transform: (T) -> R): Flow<List<R>> =
    map { list -> list.map(transform) }

fun Long.toFormattedTimestamp(): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(this))
}
