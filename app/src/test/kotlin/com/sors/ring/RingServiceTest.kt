package com.sors.ring

import com.sors.config.TriggerConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class RingServiceTest {

    @Test
    fun `trigger ring starts foreground execution and schedules auto stop`() {
        val audioController = RecordingAudioController()
        val foregroundController = RecordingForegroundExecutionController()
        val scheduler = CapturingTaskScheduler()
        val ringService = RingService(
            audioController = audioController,
            foregroundExecutionController = foregroundController,
            scheduler = scheduler,
            config = TriggerConfig(keyword = "RING_NOW", ringDurationSeconds = 30),
        )

        ringService.triggerRing()

        assertEquals(
            listOf("setNormal", "setMaxVolume", "playTone"),
            audioController.events,
        )
        assertEquals(listOf("start"), foregroundController.events)
        assertEquals(30_000L, scheduler.capturedDelayMillis)

        scheduler.runCapturedTask()

        assertEquals(
            listOf("setNormal", "setMaxVolume", "playTone", "stopTone"),
            audioController.events,
        )
        assertEquals(listOf("start", "stop"), foregroundController.events)
    }

    private class RecordingAudioController : AudioController {
        val events = mutableListOf<String>()

        override fun setRingerModeNormal() {
            events += "setNormal"
        }

        override fun setRingVolumeToMax() {
            events += "setMaxVolume"
        }

        override fun playAlertTone() {
            events += "playTone"
        }

        override fun stopAlertTone() {
            events += "stopTone"
        }
    }

    private class RecordingForegroundExecutionController : ForegroundExecutionController {
        val events = mutableListOf<String>()

        override fun startForegroundExecution() {
            events += "start"
        }

        override fun stopForegroundExecution() {
            events += "stop"
        }
    }

    private class CapturingTaskScheduler : TaskScheduler {
        var capturedDelayMillis: Long? = null
        private var task: (() -> Unit)? = null

        override fun schedule(delayMillis: Long, action: () -> Unit) {
            capturedDelayMillis = delayMillis
            task = action
        }

        fun runCapturedTask() {
            task?.invoke()
        }
    }
}
