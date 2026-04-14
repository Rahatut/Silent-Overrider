package com.rahatut.silentoverrider.storage

import android.content.Context
import com.rahatut.silentoverrider.BuildConfig

class TriggerSettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getKeyword(): String {
        val stored = prefs.getString(KEY_TRIGGER_KEYWORD, null)?.trim().orEmpty()
        return if (stored.isNotEmpty()) stored else BuildConfig.TRIGGER_KEYWORD
    }

    fun setKeyword(keyword: String): Boolean {
        val cleaned = keyword.trim()
        if (cleaned.isEmpty()) return false
        prefs.edit().putString(KEY_TRIGGER_KEYWORD, cleaned).apply()
        return true
    }

    fun getAlertDurationSeconds(): Int {
        val stored = prefs.getInt(KEY_ALERT_DURATION_SECONDS, BuildConfig.ALERT_DURATION_SECONDS)
        return stored.coerceIn(MIN_DURATION_SECONDS, MAX_DURATION_SECONDS)
    }

    fun setAlertDurationSeconds(seconds: Int): Boolean {
        if (seconds !in MIN_DURATION_SECONDS..MAX_DURATION_SECONDS) return false
        prefs.edit().putInt(KEY_ALERT_DURATION_SECONDS, seconds).apply()
        return true
    }

    companion object {
        private const val PREFS_NAME = "sors_settings_prefs"
        private const val KEY_TRIGGER_KEYWORD = "trigger_keyword"
        private const val KEY_ALERT_DURATION_SECONDS = "alert_duration_seconds"

        const val MIN_DURATION_SECONDS = 5
        const val MAX_DURATION_SECONDS = 300
    }
}
