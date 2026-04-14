package com.rahatut.silentoverrider.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.rahatut.silentoverrider.service.RingService
import com.rahatut.silentoverrider.storage.WhitelistStore
import com.rahatut.silentoverrider.util.PhoneNumberNormalizer

class CallTriggerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED != intent.action) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val rawIncoming = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).orEmpty()
        val incomingNumber = PhoneNumberNormalizer.normalize(
            rawIncoming
        )
        Log.d(TAG, "PHONE_STATE broadcast: state=$state raw=$rawIncoming normalized=$incomingNumber")

        val isEligibleState =
            state == TelephonyManager.EXTRA_STATE_RINGING ||
                state == TelephonyManager.EXTRA_STATE_OFFHOOK ||
                state == TelephonyManager.EXTRA_STATE_IDLE
        if (!isEligibleState) return

        if (incomingNumber.isBlank()) {
            Log.w(TAG, "Incoming number unavailable; cannot verify whitelist")
            return
        }

        val store = WhitelistStore(context.applicationContext)
        if (!store.contains(incomingNumber)) {
            Log.w(TAG, "Incoming number not in whitelist: $incomingNumber")
            return
        }

        val now = SystemClock.elapsedRealtime()
        if (incomingNumber == lastTriggeredNumber && now - lastTriggeredAtMs < RE_TRIGGER_BLOCK_MS) {
            Log.d(TAG, "Trigger suppressed by cooldown for number=$incomingNumber")
            return
        }
        lastTriggeredNumber = incomingNumber
        lastTriggeredAtMs = now

        val serviceIntent = Intent(context, RingService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
        Log.i(TAG, "Whitelisted incoming call matched; ring service started")
    }

    companion object {
        private const val TAG = "CallTriggerReceiver"
        private const val RE_TRIGGER_BLOCK_MS = 5000L
        private var lastTriggeredNumber: String? = null
        private var lastTriggeredAtMs: Long = 0L
    }
}
