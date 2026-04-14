package com.sors.ring

interface TaskScheduler {
    fun schedule(delayMillis: Long, action: () -> Unit)
}
