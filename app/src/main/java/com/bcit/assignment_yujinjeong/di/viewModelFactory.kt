package com.bcit.assignment_yujinjeong.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bcit.assignment_yujinjeong.viewmodel.AlarmViewModel
import com.bcit.assignment_yujinjeong.viewmodel.CardGameViewModel

/**
 * Custom ViewModel factory to create ViewModels with dependencies from AppContainer
 */
class ViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Create the appropriate ViewModel based on the requested class
        return when {
            modelClass.isAssignableFrom(AlarmViewModel::class.java) -> {
                AlarmViewModel(
                    alarmRepository = appContainer.alarmRepository,
                    alarmManager = appContainer.alarmManager
                ) as T
            }
            modelClass.isAssignableFrom(CardGameViewModel::class.java) -> {
                CardGameViewModel(
                    imageRepository = appContainer.imageRepository,
                    alarmRepository = appContainer.alarmRepository,
                    alarmManager = appContainer.alarmManager
                ) as T
            }
            // Add cases for other ViewModels as needed
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}