package com.sors.sms

import com.sors.config.TriggerConfig
import com.sors.platform.KeyValueStore
import com.sors.storage.SharedPreferencesWhitelistStore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SmsTriggerValidatorTest {

    private val normalizer = PhoneNumberNormalizer()
    private val keyValueStore = InMemoryKeyValueStore()
    private val whitelistStore = SharedPreferencesWhitelistStore(keyValueStore, normalizer)
    private val validator = SmsTriggerValidator(
        config = TriggerConfig(keyword = "RING_NOW"),
        whitelistStore = whitelistStore,
    )

    @Test
    fun `accepts trigger from authorized number and exact keyword`() {
        whitelistStore.add("+88 01712-345678")

        val result = validator.isValidTrigger(
            SmsMessage(senderAddress = "01712345678", body = "RING_NOW"),
        )

        assertTrue(result)
    }

    @Test
    fun `rejects trigger from unauthorized sender`() {
        val result = validator.isValidTrigger(
            SmsMessage(senderAddress = "01712345678", body = "RING_NOW"),
        )

        assertFalse(result)
    }

    @Test
    fun `rejects trigger with wrong keyword`() {
        whitelistStore.add("01712345678")

        val result = validator.isValidTrigger(
            SmsMessage(senderAddress = "01712345678", body = "ring-now"),
        )

        assertFalse(result)
    }

    private class InMemoryKeyValueStore : KeyValueStore {
        private val data = mutableMapOf<String, Set<String>>()

        override fun getStringSet(key: String): Set<String>? = data[key]

        override fun putStringSet(key: String, values: Set<String>) {
            data[key] = values.toSet()
        }
    }
}
