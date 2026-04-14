package com.sors.sms

import com.sors.config.TriggerConfig
import com.sors.ring.RingTrigger
import com.sors.storage.WhitelistStore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SmsReceiverTest {

    @Test
    fun `joins multipart sms body before validation`() {
        val ringTrigger = RecordingRingTrigger()
        val receiver = SmsReceiver(
            SmsTriggerProcessor(
                validator = SmsTriggerValidator(
                    config = TriggerConfig(keyword = "RING_NOW"),
                    whitelistStore = FixedWhitelistStore(setOf("1712345678")),
                ),
                ringService = ringTrigger,
            ),
        )

        val processed = receiver.onSmsReceived(
            IncomingSmsEvent(
                senderAddress = "+8801712345678",
                messageParts = listOf("RING", "_", "NOW"),
                simSlot = 1,
            ),
        )

        assertTrue(processed)
        assertTrue(ringTrigger.triggered)
    }

    @Test
    fun `rejects multipart sms if sender is not whitelisted`() {
        val ringTrigger = RecordingRingTrigger()
        val receiver = SmsReceiver(
            SmsTriggerProcessor(
                validator = SmsTriggerValidator(
                    config = TriggerConfig(keyword = "RING_NOW"),
                    whitelistStore = FixedWhitelistStore(emptySet()),
                ),
                ringService = ringTrigger,
            ),
        )

        val processed = receiver.onSmsReceived(
            IncomingSmsEvent(
                senderAddress = "+8801712345678",
                messageParts = listOf("RING_NOW"),
            ),
        )

        assertFalse(processed)
        assertFalse(ringTrigger.triggered)
    }

    private class FixedWhitelistStore(private val allowed: Set<String>) : WhitelistStore {
        private val normalizer = PhoneNumberNormalizer()

        override fun add(phoneNumber: String) = Unit

        override fun remove(phoneNumber: String) = Unit

        override fun list(): Set<String> = allowed

        override fun contains(phoneNumber: String): Boolean = allowed.contains(normalizer.normalize(phoneNumber))
    }

    private class RecordingRingTrigger : RingTrigger {
        var triggered: Boolean = false

        override fun triggerRing() {
            triggered = true
        }
    }
}
