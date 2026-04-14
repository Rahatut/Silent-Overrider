package com.sors.sms

import com.sors.config.TriggerConfig
import com.sors.storage.WhitelistStore

class SmsTriggerValidator(
    private val config: TriggerConfig,
    private val whitelistStore: WhitelistStore,
) {
    fun isValidTrigger(message: SmsMessage): Boolean {
        val normalizedBody = message.body.trim()
        if (!normalizedBody.equals(config.keyword, ignoreCase = true)) {
            return false
        }
        return whitelistStore.contains(message.senderAddress)
    }
}
