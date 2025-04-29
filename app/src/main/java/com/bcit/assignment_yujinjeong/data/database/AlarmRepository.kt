package com.bcit.assignment_yujinjeong.data.database

import com.bcit.assignment_yujinjeong.data.dataclass.Alarm
import kotlinx.coroutines.flow.Flow

/**
 * Repository to manage interaction with db.
 */
class AlarmRepository(
    private val alarmDao: AlarmDao
) {
    //These functions interact with DAO from AlarmDao and returning data from db
    fun getAllAlarms(): Flow<List<Alarm>> = alarmDao.getAllAlarms()
    fun getEnabledAlarms(): Flow<List<Alarm>> = alarmDao.getEnabledAlarms()
    suspend fun getAlarmById(alarmId: Long): Alarm? = alarmDao.getAlarmById(alarmId)
    suspend fun insertAlarm(alarm: Alarm): Long = alarmDao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: Alarm) = alarmDao.updateAlarm(alarm)
    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.deleteAlarm(alarm)
}