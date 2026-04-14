package com.sors.storage

import com.sors.platform.KeyValueStore
import com.sors.sms.PhoneNumberNormalizer

class SharedPreferencesWhitelistStore(
    private val keyValueStore: KeyValueStore,
    private val normalizer: PhoneNumberNormalizer,
    private val storageKey: String = DEFAULT_STORAGE_KEY,
) : WhitelistStore {

    override fun add(phoneNumber: String) {
        val normalized = normalizeOrThrow(phoneNumber)
        val current = readMutableSet()
        current += normalized
        keyValueStore.putStringSet(storageKey, current)
    }

    override fun remove(phoneNumber: String) {
        val normalized = normalizeOrThrow(phoneNumber)
        val current = readMutableSet()
        current -= normalized
        keyValueStore.putStringSet(storageKey, current)
    }

    override fun list(): Set<String> = readMutableSet().toSet()

    override fun contains(phoneNumber: String): Boolean {
        val normalized = normalizer.normalize(phoneNumber)
        return normalized.isNotBlank() && readMutableSet().contains(normalized)
    }

    private fun normalizeOrThrow(phoneNumber: String): String {
        val normalized = normalizer.normalize(phoneNumber)
        require(normalized.isNotBlank()) { "Phone number must contain digits" }
        return normalized
    }

    private fun readMutableSet(): MutableSet<String> =
        keyValueStore.getStringSet(storageKey)?.toMutableSet() ?: mutableSetOf()

    companion object {
        const val DEFAULT_STORAGE_KEY = "authorized_phone_numbers"
    }
}
