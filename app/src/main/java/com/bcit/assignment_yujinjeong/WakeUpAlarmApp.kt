package com.bcit.assignment_yujinjeong

import android.app.Application
import android.util.Log
import com.bcit.assignment_yujinjeong.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Yujin Jeong, A01310724
 */

/**
 * This class is for base application class. And helping runs before other functionalities for this
 * in the app and making sure it's alive during the entire time my app running.
 * Basically, setting up foundation for entire app, making different parts communicate.
 */
class WakeUpAlarmApplication : Application() {

    // Lazy initialize our AppContainer
    lateinit var appContainer: AppContainer
        private set

    /**
     * Initializing dependencies and setting up alarms.
     */
    override fun onCreate() {
        super.onCreate()

        //Initialize the app container
        appContainer = AppContainer(applicationContext)
        appContainer.alarmManager.initialize()

        //For logging purposes,
        CoroutineScope(Dispatchers.IO).launch {
            val alarms = appContainer.alarmRepository.getAllAlarms()

            alarms.collect { alarmList ->

                alarmList.forEach { alarm ->
                }
            }
        }
    }
}