package com.sors.ring

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ScheduledExecutorTaskScheduler(
    private val executorService: ScheduledExecutorService,
) : TaskScheduler {
    override fun schedule(delayMillis: Long, action: () -> Unit) {
        executorService.schedule(action, delayMillis, TimeUnit.MILLISECONDS)
    }
}
