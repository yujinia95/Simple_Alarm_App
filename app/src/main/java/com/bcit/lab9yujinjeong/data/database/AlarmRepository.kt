package com.bcit.lab9yujinjeong.data.database

import android.content.Context
import com.bcit.lab9yujinjeong.data.dataclass.Alarm
import kotlinx.coroutines.flow.Flow

/**
 * Singleton object to manage interaction with db.
 */
object AlarmRepository {

    private var db: AlarmDatabase? = null
    private var dao: AlarmDao?     = null


    //Ensuing db and DAO initialized only once (SINGLETON!!)
    fun initialize(context: Context) {

        if (db == null) {
            db = AlarmDatabase.getInstance(context.applicationContext)
            dao = db?.alarmDao()
        }

        //This function returns AlarmDao instance, if dao is null throw exception!(high chance db
        // is not initialized :/)
        fun getDao(): AlarmDao {
            return dao ?: throw IllegalStateException(
                "Not able to use getDao(), initialize first(Check AlarmRepository.kt)"
            )
        }

        //These functions interact with DAO from AlarmDao and returning data from db
        fun getAllAlarms(): Flow<List<Alarm>> = getDao().getAllAlarms()
        fun getEnabledAlarms(): Flow<List<Alarm>> = getDao().getEnabledAlarms()
        suspend fun getAlarmById(alarmId: Int): Alarm? = getDao().getAlarmById(alarmId)
        suspend fun insertAlarm(alarm: Alarm): Int = getDao().insertAlarm(alarm)
        suspend fun updateAlarm(alarm: Alarm) = getDao().updateAlarm(alarm)
        suspend fun deleteAlarm(alarm: Alarm) = getDao().deleteAlarm(alarm)
    }
}