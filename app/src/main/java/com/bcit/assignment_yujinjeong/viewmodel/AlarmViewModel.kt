package com.bcit.assignment_yujinjeong.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcit.assignment_yujinjeong.alarm.AlarmManager
import com.bcit.assignment_yujinjeong.data.database.AlarmRepository
import com.bcit.assignment_yujinjeong.data.dataclass.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.DayOfWeek

/**
 * ViewModel for alarm-related functionality, without Hilt dependency
 */
class AlarmViewModel(
    private val alarmRepository: AlarmRepository,
    private val alarmManager: AlarmManager
) : ViewModel() {

    // Exposing the Flow directly
    val alarms: Flow<List<Alarm>> = alarmRepository.getAllAlarms()

    fun addAlarm(hour: Int, minute: Int, label: String = "", repeatDays: Set<DayOfWeek> = emptySet()) {
        viewModelScope.launch {
            val alarm = Alarm(
                hour = hour,
                minute = minute,
                isEnabled = true,
                label = label,
                repeatDays = repeatDays
            )
            val id = alarmRepository.insertAlarm(alarm)

            // After inserting, retrieve the alarm with the generated ID and schedule it
            alarmRepository.getAlarmById(id)?.let { newAlarm ->
                alarmManager.scheduleAlarm(newAlarm)
            }
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmRepository.updateAlarm(alarm)

            if (alarm.isEnabled) {
                alarmManager.scheduleAlarm(alarm)
            } else {
                alarmManager.cancelAlarm(alarm)
            }
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmManager.cancelAlarm(alarm)
            alarmRepository.deleteAlarm(alarm)
        }
    }

    fun toggleAlarmEnabled(alarm: Alarm) {
        viewModelScope.launch {
            val updatedAlarm = alarm.copy(isEnabled = !alarm.isEnabled)
            alarmRepository.updateAlarm(updatedAlarm)

            if (updatedAlarm.isEnabled) {
                alarmManager.scheduleAlarm(updatedAlarm)
            } else {
                alarmManager.cancelAlarm(updatedAlarm)
            }
        }
    }
}