package com.sors.storage

import com.sors.platform.KeyValueStore
import com.sors.sms.PhoneNumberNormalizer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SharedPreferencesWhitelistStoreTest {
    private val keyValueStore = InMemoryKeyValueStore()
    private val store = SharedPreferencesWhitelistStore(keyValueStore, PhoneNumberNormalizer())

    @Test
    fun `add and contains use normalized phone format`() {
        store.add("+88 01712-345678")

        assertTrue(store.contains("01712345678"))
        assertTrue(store.contains("+8801712345678"))
    }

    @Test
    fun `remove deletes normalized value`() {
        store.add("01712345678")
        store.remove("+8801712345678")

        assertFalse(store.contains("01712345678"))
    }

    @Test
    fun `list returns persisted values`() {
        store.add("01712345678")
        store.add("01999999999")

        assertEquals(setOf("1712345678", "1999999999"), store.list())
    }

    private class InMemoryKeyValueStore : KeyValueStore {
        private val data = mutableMapOf<String, Set<String>>()

        override fun getStringSet(key: String): Set<String>? = data[key]

        override fun putStringSet(key: String, values: Set<String>) {
            data[key] = values.toSet()
        }
    }
}
