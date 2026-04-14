package com.sors.platform

interface KeyValueStore {
    fun getStringSet(key: String): Set<String>?
    fun putStringSet(key: String, values: Set<String>)
}
