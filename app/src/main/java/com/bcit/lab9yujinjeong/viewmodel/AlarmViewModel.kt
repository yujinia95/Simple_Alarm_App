package com.bcit.lab9yujinjeong.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bcit.lab9yujinjeong.data.database.AlarmDao
import com.bcit.lab9yujinjeong.data.dataclass.Alarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek


/**
 * This class is for viewModel for managing alarms.
 * Responsible for handling alarm-related operations.
 */
class AlarmViewModel(

    private val alarmDao: AlarmDao

//ViewModel persists data across configuration changes. (UI related data separate from UI
// Controller).
) :ViewModel() {

    //Flow handles asynchronous and work with coroutines (emits values only when collected).
    //Flow updates UI automatically when data changes.
    val alarms: Flow<List<Alarm>> = alarmDao.getAllAlarms()

    /**
     * Creating a new alarm and stores in database.
     *
     * Why I need coroutine?
     * - Database operation is slow, so must be done off the main thread(avoid freezing UI)
     */
    fun addAlarm(hour: Int,
                 minute: Int,
                 repeatDays: Set<DayOfWeek> = emptySet(),
                 label: String = "") {

        //Launching coroutine inside ViewModelScope.
        //Any coroutine launched in this scope is automatically canceled if ViewModel is cleared.
        viewModelScope.launch {

            //Dispatchers.IO is optimized to perform network I/O outside of the main thread
            withContext(Dispatchers.IO) {
                val alarm = Alarm(hour = hour, minute = minute,
                    repeatDays = repeatDays, label = label)

                alarmDao.insertAlarm(alarm)
            }
        }
    }

    /**
     * Updating existed alarm.
     */
    fun updateAlarm(alarm: Alarm) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                alarmDao.updateAlarm(alarm)
            }
        }
    }


    /**
     * toggles the enable button
     */
    fun toggleAlarmEnabled(alarm: Alarm) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                //Data classes immutable, so .copy() allows to create a new modified obj instead
                //changing existing one(with opposite isEnabled value).
                alarmDao.updateAlarm(alarm.copy(isEnabled = !alarm.isEnabled))
            }
        }
    }


    /**
     * Removing alarm from DB
     */
    fun deleteAlarm (alarm: Alarm) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                alarmDao.deleteAlarm(alarm)
            }
        }
    }

    /**
     * Creating alarm viewModel instance. Make defining methods and properties are allowed without
     * creating instance of the class.
     *
     * Generic allows me to return different ViewModels.
     *
     * My AlarmViewModel has argument constructor, so have to use Factory. Which makes Android to
     * create ViewModel.
     *
     * Returns ViewModelProvider.Factory(ViewModel instance).
     */
    companion object {

        fun alarmViewModelFactory(alarmDao: AlarmDao): ViewModelProvider.Factory =

            //Creating anonymous inner factory class and implementing
            object: ViewModelProvider.Factory {

                //Override create method(generic method)
                //"UNCHECKED_CAST" removes warning about casting AlarmViewModel as T.
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {

                    return AlarmViewModel(alarmDao) as T
                }
        }
    }
}
















