package com.rahatut.silentoverrider.validation

import com.rahatut.silentoverrider.storage.WhitelistStore

class TriggerValidator(
    private val whitelistStore: WhitelistStore,
    private val keyword: String
) {
    fun isValid(sender: String?, body: String?): Boolean {
        if (sender.isNullOrBlank() || body.isNullOrBlank()) return false
        val trimmedBody = body.trim()
        return trimmedBody.equals(keyword.trim(), ignoreCase = true) && whitelistStore.contains(sender)
    }
}
