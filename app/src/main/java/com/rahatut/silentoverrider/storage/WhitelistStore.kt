package com.rahatut.silentoverrider.storage

import android.content.Context
import com.rahatut.silentoverrider.util.PhoneNumberNormalizer

class WhitelistStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAll(): List<String> =
        prefs.getStringSet(KEY_NUMBERS, emptySet()).orEmpty().toList().sorted()

    fun add(number: String): Boolean {
        val normalized = PhoneNumberNormalizer.normalize(number)
        if (normalized.isBlank()) return false

        val updated = prefs.getStringSet(KEY_NUMBERS, emptySet()).orEmpty().toMutableSet()
        val added = updated.add(normalized)
        prefs.edit().putStringSet(KEY_NUMBERS, updated).apply()
        return added
    }

    fun remove(number: String): Boolean {
        val normalized = PhoneNumberNormalizer.normalize(number)
        val updated = prefs.getStringSet(KEY_NUMBERS, emptySet()).orEmpty().toMutableSet()
        val removed = updated.remove(normalized)
        prefs.edit().putStringSet(KEY_NUMBERS, updated).apply()
        return removed
    }

    fun contains(number: String): Boolean {
        val normalized = PhoneNumberNormalizer.normalize(number)
        return prefs.getStringSet(KEY_NUMBERS, emptySet()).orEmpty().contains(normalized)
    }

    companion object {
        private const val PREFS_NAME = "sors_whitelist_prefs"
        private const val KEY_NUMBERS = "authorized_numbers"
    }
}
