package com.sors.ui

import com.sors.storage.WhitelistStore

class WhitelistManager(
    private val whitelistStore: WhitelistStore,
) {
    fun addPhoneNumber(phoneNumber: String) = whitelistStore.add(phoneNumber)

    fun removePhoneNumber(phoneNumber: String) = whitelistStore.remove(phoneNumber)

    fun getAuthorizedPhoneNumbers(): List<String> = whitelistStore.list().sorted()
}
