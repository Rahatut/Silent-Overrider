package com.rahatut.silentoverrider.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.core.content.ContextCompat
import com.rahatut.silentoverrider.service.RingService
import com.rahatut.silentoverrider.storage.TriggerSettingsStore
import com.rahatut.silentoverrider.storage.WhitelistStore
import com.rahatut.silentoverrider.validation.TriggerValidator

class SmsTriggerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION != intent.action) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return

        val sender = messages.firstOrNull()?.displayOriginatingAddress
        val body = messages.joinToString(separator = "") { it.messageBody.orEmpty() }

        val settings = TriggerSettingsStore(context.applicationContext)
        val validator = TriggerValidator(
            whitelistStore = WhitelistStore(context.applicationContext),
            keyword = settings.getKeyword()
        )

        if (!validator.isValid(sender, body)) return

        val serviceIntent = Intent(context, RingService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
