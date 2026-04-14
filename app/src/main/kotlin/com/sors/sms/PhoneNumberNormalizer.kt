package com.sors.sms

class PhoneNumberNormalizer {
    fun normalize(raw: String): String {
        if (raw.isBlank()) return ""

        var digits = raw.filter(Char::isDigit)
        if (digits.startsWith("00")) {
            digits = digits.drop(2)
        }

        return when {
            digits.length > LOCAL_NUMBER_LENGTH -> digits.takeLast(LOCAL_NUMBER_LENGTH)
            else -> digits
        }
    }

    companion object {
        private const val LOCAL_NUMBER_LENGTH = 10
    }
}
