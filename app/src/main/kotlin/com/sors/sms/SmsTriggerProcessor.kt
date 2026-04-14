package com.sors.sms

import com.sors.ring.RingTrigger

class SmsTriggerProcessor(
    private val validator: SmsTriggerValidator,
    private val ringService: RingTrigger,
) {
    fun process(message: SmsMessage): Boolean {
        if (!validator.isValidTrigger(message)) {
            return false
        }
        ringService.triggerRing()
        return true
    }
}
