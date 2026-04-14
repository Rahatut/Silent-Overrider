package com.sors.platform

class InMemoryKeyValueStore : KeyValueStore {
    private val data = mutableMapOf<String, Set<String>>()

    override fun getStringSet(key: String): Set<String>? = data[key]

    override fun putStringSet(key: String, values: Set<String>) {
        data[key] = values.toSet()
    }
}
