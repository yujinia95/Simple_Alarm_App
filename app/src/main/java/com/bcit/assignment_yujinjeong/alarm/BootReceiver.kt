package com.bcit.assignment_yujinjeong.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bcit.assignment_yujinjeong.WakeUpAlarmApplication


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val appContainer = (context.applicationContext as WakeUpAlarmApplication).appContainer

            // Ensure alarms are initialized on boot
            appContainer.alarmManager.initialize()
        }
    }
}