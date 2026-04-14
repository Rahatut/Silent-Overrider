package com.rahatut.silentoverrider.validation

import com.rahatut.silentoverrider.storage.WhitelistStore

class TriggerValidator(
    private val keyword: String,
    private val senderAuthorizer: (String) -> Boolean
) {
    constructor(whitelistStore: WhitelistStore, keyword: String) : this(
        keyword = keyword,
        senderAuthorizer = whitelistStore::contains
    )

    fun isValid(sender: String?, body: String?): Boolean {
        if (sender.isNullOrBlank() || body.isNullOrBlank()) return false
        val trimmedBody = body.trim()
        return trimmedBody.equals(keyword.trim(), ignoreCase = true) && senderAuthorizer(sender)
    }
}
