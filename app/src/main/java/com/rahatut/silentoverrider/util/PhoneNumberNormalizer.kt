package com.rahatut.silentoverrider.util

object PhoneNumberNormalizer {

    fun normalize(input: String?): String {
        if (input.isNullOrBlank()) return ""

        val digits = input.filter(Char::isDigit)
        if (digits.isBlank()) return ""

        val withoutCountryPrefix = when {
            digits.startsWith("88") && digits.length > 11 -> digits.removePrefix("88")
            digits.startsWith("0") && digits.length > 10 -> digits.drop(1)
            else -> digits
        }

        return withoutCountryPrefix.trimStart('0')
    }
}
