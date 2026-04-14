package com.sors.sms

import kotlin.test.Test
import kotlin.test.assertEquals

class PhoneNumberNormalizerTest {
    private val normalizer = PhoneNumberNormalizer()

    @Test
    fun `normalizes number with country code and symbols`() {
        assertEquals("1712345678", normalizer.normalize("+88 01712-345678"))
    }

    @Test
    fun `normalizes number with international dial prefix`() {
        assertEquals("1712345678", normalizer.normalize("0088-01712345678"))
    }

    @Test
    fun `returns empty value for non numeric input`() {
        assertEquals("", normalizer.normalize("( ) -"))
    }
}
