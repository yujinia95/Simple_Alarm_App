package com.bcit.assignment_yujinjeong.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcit.assignment_yujinjeong.WakeUpAlarmApplication
import com.bcit.assignment_yujinjeong.viewmodel.AlarmViewModel
import com.bcit.assignment_yujinjeong.viewmodel.CardGameViewModel

/**
 * Extension functions to easily get ViewModels in Compose without Hilt
 */

/**
 * Get the application's AppContainer from the current Compose context
 */
@Composable
fun getAppContainer(): AppContainer {
    val context = LocalContext.current
    return (context.applicationContext as WakeUpAlarmApplication).appContainer
}

/**
 * Get the AlarmViewModel from the current Compose context
 */
@Composable
fun alarmViewModel(): AlarmViewModel {
    val appContainer = getAppContainer()
    return viewModel(
        factory = ViewModelFactory(appContainer)
    )
}

/**
 * Get the CardGameViewModel from the current Compose context
 */
@Composable
fun cardGameViewModel(): CardGameViewModel {
    val appContainer = getAppContainer()
    return viewModel(
        factory = ViewModelFactory(appContainer)
    )
}

