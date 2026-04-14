package com.rahatut.silentoverrider

import com.rahatut.silentoverrider.validation.TriggerValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TriggerValidatorTest {

    private val whitelisted = setOf("1712345678")

    @Test
    fun validatesAuthorizedSenderAndKeyword() {
        val validator = TriggerValidator("OVERRIDE_RING") { sender ->
            sender.filter(Char::isDigit).trimStart('0') in whitelisted
        }
        assertTrue(validator.isValid("01712345678", " override_ring "))
    }

    @Test
    fun rejectsUnauthorizedSender() {
        val validator = TriggerValidator("OVERRIDE_RING") { sender ->
            sender.filter(Char::isDigit).trimStart('0') in whitelisted
        }
        assertFalse(validator.isValid("01999999999", "OVERRIDE_RING"))
    }
}
