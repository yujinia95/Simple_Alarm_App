package com.bcit.assignment_yujinjeong.alarm


import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.provider.Settings
import android.media.AudioAttributes
import android.media.MediaPlayer

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        // Add a static method to stop the service from outside
        fun stopService(context: Context) {
            val intent = Intent(context, AlarmService::class.java)
            context.stopService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Play the alarm sound
        playAlarmSound()

        // Keep the service running until explicitly stopped
        return START_STICKY
    }

    private fun playAlarmSound() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    )
                    setDataSource(applicationContext, Settings.System.DEFAULT_ALARM_ALERT_URI)
                    isLooping = true
                    prepare()
                    start()
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmService", "Error playing alarm sound", e)
            // Try fallback
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build()
                    )
                    setDataSource(applicationContext, Settings.System.DEFAULT_NOTIFICATION_URI)
                    isLooping = true
                    prepare()
                    start()
                }
                Log.d("AlarmService", "Started playing fallback sound")
            } catch (e: Exception) {
                Log.e("AlarmService", "Error playing fallback sound", e)
            }
        }
    }

    override fun onDestroy() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("AlarmService", "Error stopping media player", e)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}