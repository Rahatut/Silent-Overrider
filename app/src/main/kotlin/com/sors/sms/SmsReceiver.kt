package com.sors.sms

class SmsReceiver(
    private val triggerProcessor: SmsTriggerProcessor,
) {
    fun onSmsReceived(event: IncomingSmsEvent): Boolean {
        val message = SmsMessage(
            senderAddress = event.senderAddress,
            body = event.fullMessageBody,
        )
        return triggerProcessor.process(message)
    }
}
