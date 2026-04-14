package com.sors

import com.sors.config.TriggerConfig
import com.sors.platform.InMemoryKeyValueStore
import com.sors.ring.AudioController
import com.sors.ring.ForegroundExecutionController
import com.sors.ring.RingService
import com.sors.ring.ScheduledExecutorTaskScheduler
import com.sors.sms.IncomingSmsEvent
import com.sors.sms.PhoneNumberNormalizer
import com.sors.sms.SmsReceiver
import com.sors.sms.SmsTriggerProcessor
import com.sors.sms.SmsTriggerValidator
import com.sors.storage.SharedPreferencesWhitelistStore
import java.util.concurrent.Executors

fun main() {
    val config = TriggerConfig(keyword = "RING_NOW", ringDurationSeconds = 2)
    val whitelistStore = SharedPreferencesWhitelistStore(
        keyValueStore = InMemoryKeyValueStore(),
        normalizer = PhoneNumberNormalizer(),
    )
    whitelistStore.add("+88 01712-345678")

    val executor = Executors.newSingleThreadScheduledExecutor()
    val ringService = RingService(
        audioController = ConsoleAudioController,
        foregroundExecutionController = ConsoleForegroundExecutionController,
        scheduler = ScheduledExecutorTaskScheduler(executor),
        config = config,
    )

    val receiver = SmsReceiver(
        SmsTriggerProcessor(
            validator = SmsTriggerValidator(config, whitelistStore),
            ringService = ringService,
        ),
    )

    val accepted = receiver.onSmsReceived(
        IncomingSmsEvent(
            senderAddress = "+8801712345678",
            messageParts = listOf("RING_NOW"),
            simSlot = 1,
        ),
    )

    println("Trigger accepted: $accepted")

    Thread.sleep((config.ringDurationSeconds + 1) * 1000)
    executor.shutdownNow()
}

private object ConsoleAudioController : AudioController {
    override fun setRingerModeNormal() {
        println("Ringer mode set to normal")
    }

    override fun setRingVolumeToMax() {
        println("Ring volume set to maximum")
    }

    override fun playAlertTone() {
        println("Playing alert tone")
    }

    override fun stopAlertTone() {
        println("Stopping alert tone")
    }
}

private object ConsoleForegroundExecutionController : ForegroundExecutionController {
    override fun startForegroundExecution() {
        println("Foreground execution started")
    }

    override fun stopForegroundExecution() {
        println("Foreground execution stopped")
    }
}
