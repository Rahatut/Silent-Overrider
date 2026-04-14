package com.sors.ring

import com.sors.config.TriggerConfig

class RingService(
    private val audioController: AudioController,
    private val foregroundExecutionController: ForegroundExecutionController,
    private val scheduler: TaskScheduler,
    private val config: TriggerConfig,
) : RingTrigger {

    override fun triggerRing() {
        foregroundExecutionController.startForegroundExecution()
        audioController.setRingerModeNormal()
        audioController.setRingVolumeToMax()
        audioController.playAlertTone()

        scheduler.schedule(config.ringDurationSeconds * 1000) {
            stopRing()
        }
    }

    fun stopRing() {
        audioController.stopAlertTone()
        foregroundExecutionController.stopForegroundExecution()
    }
}
