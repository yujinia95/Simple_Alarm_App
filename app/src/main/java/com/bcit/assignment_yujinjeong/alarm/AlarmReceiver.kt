package com.bcit.assignment_yujinjeong.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import com.bcit.assignment_yujinjeong.MainActivity

/**
 * BroadcastReceiver for handling alarm triggers.
 */
class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private var currentMediaPlayer: MediaPlayer? = null

        /**
         * Stops the alarm sound and releases the media player.
         */
        fun stopAlarm(context: Context) {

            // Stop the media player if it exists
            currentMediaPlayer?.apply {
                try {
                    if (isPlaying) {
                        stop()
                        release()
                    }
                } catch (e: Exception) {
                    Log.e("AlarmReceiver", "Error stopping media player", e)
                }
            }
            currentMediaPlayer = null

            // Stop the alarm service
            val serviceIntent = Intent(context, AlarmService::class.java)
            val stopped = context.stopService(serviceIntent)
        }
    }
    /**
     * Handles the alarm trigger.
     * Starts the alarm sound and launches the game activity.
     */
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Don't start the alarm sound or game screen, just reschedule alarms
            return
        }

        val alarmId = intent.getIntExtra("ALARM_ID", -1)

        val serviceIntent = Intent(context, AlarmService::class.java)
        serviceIntent.putExtra("ALARM_ID", alarmId)

        //Launch the game activity.
        val gameIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("SHOW_GAME", true)
            putExtra("ALARM_ID", alarmId)
        }
        context.startActivity(gameIntent)
    }
}