package com.bcit.assignment_yujinjeong.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bcit.assignment_yujinjeong.data.dataclass.Alarm
import kotlinx.coroutines.flow.Flow

/**
 * This interface(Data Access Object) is responsible for defining methods for database operations.
 */
@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    //Flow helps retrieve and update alarm list asynchronously.
    fun getAllAlarms(): Flow<List<Alarm>>

    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    //Making function asynchronous for reading from database
    suspend fun getAlarmById(alarmId: Long): Alarm?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //Parameter onConflict replace old data and continue transaction by using constant REPLACE.
    //Making function asynchronous for writing new alarm from database and return ID
    suspend fun insertAlarm(alarm: Alarm): Long

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarms WHERE isEnabled = 1")
    fun getEnabledAlarms(): Flow<List<Alarm>>
}