package com.bcit.assignment_yujinjeong.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.bcit.assignment_yujinjeong.data.database.AlarmRepository
import com.bcit.assignment_yujinjeong.data.dataclass.Alarm
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * This class handles scheduling alarms at specific time and making sure it works well with android
 * system.
 */
class AlarmScheduler(
    private val context: Context,
    private val alarmRepository: AlarmRepository
) {
    //Getting AlarmManager sys service used for scheduling alarm in android.
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * if alarm is not enable, cancel exisitng alarm.
     */
    fun schedule(alarm: Alarm) {

        if (!alarm.isEnabled) {
            cancel(alarm)
            return
        }


        //Creating intent(like msg) to AlarmReceiver for using when alarm goes off.
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
        }

        //This PendingIntent(msg like i want to do this later) used for AlarmManager to make broadcast
        // to AlarmReceiver.
        //FLAG_UPDATE_CURRENT --> If PendingIntent exists, update it.
        //FLAG_IMMUTABLE --> Making PendingIntent immutable
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val nextAlarmTime = getNextAlarmTime(alarm)

        try {
            // Check if we can schedule exact alarms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                scheduleInexactAlarm(alarm, pendingIntent, nextAlarmTime)
                return
            }

            // Try to schedule exact alarm
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Security exception when scheduling alarm: ${e.message}")
            scheduleInexactAlarm(alarm, pendingIntent, nextAlarmTime)
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Failed to schedule alarm: ${e.message}")
        }
    }

    private fun scheduleInexactAlarm(alarm: Alarm, pendingIntent: PendingIntent, triggerTime: Long) {
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES, // Fallback interval
            pendingIntent
        )
    }

    /**
     * Making sure i use AlarmRepository
     */
    suspend fun scheduleAllEnabledAlarms() {
        try {
            // Print out ALL alarms, not just enabled ones
            val allAlarms = alarmRepository.getAllAlarms().first()
            allAlarms.forEach { alarm ->
                Log.d("AlarmScheduler", "ALL Alarm details - ID: ${alarm.id}, Hour: ${alarm.hour}, Minute: ${alarm.minute}, Enabled: ${alarm.isEnabled}")
            }

            // Then get enabled alarms
            val enabledAlarms = alarmRepository.getEnabledAlarms().first()

            enabledAlarms.forEach { alarm ->
                Log.d("AlarmScheduler", "ENABLED Alarm details - ID: ${alarm.id}, Hour: ${alarm.hour}, Minute: ${alarm.minute}, Enabled: ${alarm.isEnabled}")
                schedule(alarm)
            }
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error scheduling alarms", e)
        }

    }

    /**
     * For removes scheduled alarm.
     */
    fun cancel(alarm: Alarm) {

        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d("AlarmScheduler", "Alarm ${alarm.id} cancelled")

    }

    /**
     * helper function to get next alarm time
     */
    private fun getNextAlarmTime(alarm: Alarm): Long {

        val now = LocalDateTime.now()

        var alarmTime = LocalDateTime.now()
            .withHour(alarm.hour)
            .withMinute(alarm.minute)
            .withSecond(0)
            .withNano(0)


        //Checking if alarm time is in the past, set it for trmw.
        if (alarmTime.isBefore(now)) {
            alarmTime = alarmTime.plusDays(1)
            Log.d("AlarmScheduler", "Alarm time was in the past. Moved to tomorrow.")

        }


        //If there are repeat days, adjust alarm to next matching day.
        if (alarm.repeatDays.isNotEmpty()) {


            //Getting current day
            val currentDayOfWeek = now.dayOfWeek


            //Calculating how many days from today for next repeat day.
            val sortedDays = alarm.repeatDays.sortedWith(compareBy {
                //Monday starting index 1.
                (it.value - currentDayOfWeek.value + 7) % 7
            })

            //For finding next day which alarm should trigger.
            val nextDay = sortedDays.firstOrNull {


                //Trying to find if there is alarm today(only not passed yet) or day in current week
                it == currentDayOfWeek && alarmTime.isAfter(now) ||
                        it.value > currentDayOfWeek.value
                //If nothing found, use first day in sorted list. (earliest day in next week).
            } ?: sortedDays.first()


            //Adjust alarm time IF alarm set up time is today but hasn't passed yet, keep the
            // current alarm time.
            alarmTime = if (nextDay == currentDayOfWeek && alarmTime.isAfter(now)) {
                alarmTime
            } else {
                //Else, find next alarm repeat day by using temporal adjusters (java library).
                alarmTime.with(TemporalAdjusters.next(nextDay))
            }

        }

        //Converting alarm time obj in my timezone, and converts it into ms. Cuz AlarmManager takes
        // ms.
        val alarmTimeMs = alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()


        return alarmTimeMs
    }

    /**
     * Helper function to format time for logging
     */
    private fun formatTime(timeMs: Long): String {
        val dateTime = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(timeMs),
            ZoneId.systemDefault()
        )
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

}