package com.sors.sms

data class IncomingSmsEvent(
    val senderAddress: String,
    val messageParts: List<String>,
    val simSlot: Int? = null,
) {
    val fullMessageBody: String = messageParts.joinToString(separator = "")
}
