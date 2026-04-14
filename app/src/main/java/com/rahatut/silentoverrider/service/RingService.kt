package com.rahatut.silentoverrider.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.rahatut.silentoverrider.BuildConfig
import com.rahatut.silentoverrider.R

class RingService : Service() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null

    private var previousRingerMode: Int = AudioManager.RINGER_MODE_NORMAL
    private var previousAlarmVolume: Int = 0
    private var previousRingVolume: Int = 0

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        createNotificationChannelIfNeeded()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        forceAudibleAlert()
        scheduleStop()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(null)
        stopAlert()
        restoreAudioState()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun forceAudibleAlert() {
        val manager = audioManager ?: return

        previousRingerMode = manager.ringerMode
        previousAlarmVolume = manager.getStreamVolume(AudioManager.STREAM_ALARM)
        previousRingVolume = manager.getStreamVolume(AudioManager.STREAM_RING)

        manager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        manager.setStreamVolume(AudioManager.STREAM_RING, manager.getStreamMaxVolume(AudioManager.STREAM_RING), 0)
        manager.setStreamVolume(AudioManager.STREAM_ALARM, manager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0)

        val alarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI ?: Settings.System.DEFAULT_RINGTONE_URI
        startAlert(alarmUri)
    }

    private fun startAlert(uri: Uri?) {
        stopAlert()

        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_ALARM)
            isLooping = true
            setDataSource(this@RingService, uri ?: Settings.System.DEFAULT_RINGTONE_URI)
            prepare()
            start()
        }
    }

    private fun stopAlert() {
        mediaPlayer?.run {
            if (isPlaying) stop()
            reset()
            release()
        }
        mediaPlayer = null
    }

    private fun restoreAudioState() {
        val manager = audioManager ?: return
        manager.ringerMode = previousRingerMode
        manager.setStreamVolume(AudioManager.STREAM_ALARM, previousAlarmVolume, 0)
        manager.setStreamVolume(AudioManager.STREAM_RING, previousRingVolume, 0)
    }

    private fun scheduleStop() {
        mainHandler.postDelayed({ stopSelf() }, BuildConfig.ALERT_DURATION_SECONDS * 1000L)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "sors_ring_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
