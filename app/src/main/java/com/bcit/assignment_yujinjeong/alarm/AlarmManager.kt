package com.bcit.assignment_yujinjeong.alarm

import android.content.Context
import android.util.Log
import com.bcit.assignment_yujinjeong.data.database.AlarmRepository
import com.bcit.assignment_yujinjeong.data.dataclass.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Responsible for managing the scheduling and canceling of alarms in the app.
 */
class AlarmManager(
    private val context: Context,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) {

    /**
     * Initializes the AlarmManager.
     * If no enabled alarms are found, an error log is displayed.
     */
    fun initialize() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allAlarms = alarmRepository.getAllAlarms().first()
                allAlarms.forEach { alarm ->
                    Log.d("AlarmManager", "ALL Alarm details - ID: ${alarm.id}, Enabled: " +
                            "${alarm.isEnabled}, Hour: ${alarm.hour}, Minute: ${alarm.minute}")
                }

                val enabledAlarms = alarmRepository.getEnabledAlarms().first()

                if (enabledAlarms.isEmpty()) {
                    Log.e("AlarmManager",  "NO ENABLED ALARMS FOUND!")
                }

                for (alarm in enabledAlarms) {
                    alarmScheduler.schedule(alarm)
                }
            } catch (e: Exception) {
                Log.e("AlarmManager", "Error in initialization", e)
            }
        }
    }

    /**
     * Schedules a specific alarm using the AlarmScheduler.
     */
    fun scheduleAlarm(alarm: Alarm) {
        CoroutineScope(Dispatchers.IO).launch {
            alarmScheduler.schedule(alarm)
        }
    }

    /**
     * Cancels a specific alarm using the AlarmScheduler.
     */
    fun cancelAlarm(alarm: Alarm) {
        CoroutineScope(Dispatchers.IO).launch {
            alarmScheduler.cancel(alarm)
        }
    }

    /**
     * Schedules all enabled alarms using the AlarmScheduler.
     */
    fun scheduleAllEnabledAlarms() {
        CoroutineScope(Dispatchers.IO).launch {
            alarmScheduler.scheduleAllEnabledAlarms()
        }
    }
}