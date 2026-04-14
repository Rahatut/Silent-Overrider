package com.sors.storage

interface WhitelistStore {
    fun add(phoneNumber: String)
    fun remove(phoneNumber: String)
    fun list(): Set<String>
    fun contains(phoneNumber: String): Boolean
}
