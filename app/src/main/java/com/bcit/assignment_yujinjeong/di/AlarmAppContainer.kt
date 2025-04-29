package com.bcit.assignment_yujinjeong.di

import android.content.Context
import com.bcit.assignment_yujinjeong.alarm.AlarmManager
import com.bcit.assignment_yujinjeong.alarm.AlarmScheduler
import com.bcit.assignment_yujinjeong.data.database.AlarmDao
import com.bcit.assignment_yujinjeong.data.database.AlarmDatabase
import com.bcit.assignment_yujinjeong.data.database.AlarmRepository
import com.bcit.assignment_yujinjeong.data.imageAPI.ImageRepository

/**
 * Manual dependency injection container that holds dependencies.
 */
class AppContainer(applicationContext: Context) {

    private val alarmDatabase: AlarmDatabase = AlarmDatabase.getInstance(applicationContext)

    val alarmDao: AlarmDao = alarmDatabase.alarmDao()

    val alarmRepository: AlarmRepository = AlarmRepository(alarmDao)

    val alarmScheduler: AlarmScheduler = AlarmScheduler(applicationContext, alarmRepository)

    val alarmManager: AlarmManager = AlarmManager(applicationContext, alarmRepository, alarmScheduler)

    val imageRepository: ImageRepository = ImageRepository.getInstance()

    init {
        alarmManager.initialize()
    }
}