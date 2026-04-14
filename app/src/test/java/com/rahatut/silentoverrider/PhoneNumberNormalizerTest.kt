package com.rahatut.silentoverrider

import com.rahatut.silentoverrider.util.PhoneNumberNormalizer
import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNumberNormalizerTest {

    @Test
    fun normalizesBangladeshCountryCodeAndSymbols() {
        assertEquals("1712345678", PhoneNumberNormalizer.normalize("+88 01712-345678"))
    }

    @Test
    fun returnsEmptyWhenInputInvalid() {
        assertEquals("", PhoneNumberNormalizer.normalize(" -- "))
    }
}
