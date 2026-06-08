package com.sentinel.util

/**
 * Normalizes password input so Persian/Arabic digits match Latin digits after reboot.
 */
object PasswordNormalizer {
    fun normalize(raw: String): String = raw.trim().map { ch ->
        when (ch) {
            '۰', '٠' -> '0'
            '۱', '١' -> '1'
            '۲', '٢' -> '2'
            '۳', '٣' -> '3'
            '۴', '٤' -> '4'
            '۵', '٥' -> '5'
            '۶', '٦' -> '6'
            '۷', '٧' -> '7'
            '۸', '٨' -> '8'
            '۹', '٩' -> '9'
            else -> ch
        }
    }.joinToString("")
}
