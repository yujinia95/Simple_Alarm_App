package com.bcit.lab9yujinjeong.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bcit.lab9yujinjeong.ui.components.AlarmItem
import com.bcit.lab9yujinjeong.ui.components.BottomBar
import com.bcit.lab9yujinjeong.ui.components.EmptyAlarmList
import com.bcit.lab9yujinjeong.ui.components.TopBar
import com.bcit.lab9yujinjeong.viewmodel.AlarmViewModel

/**
 * Creating screen for Home.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

    onAddAlarmClick: () -> Unit,
    onEditAlarmClick: (Int) -> Unit,

    //'hiltViewModel' helps me getting ViewModel instance injected. (Library)
    viewModel: AlarmViewModel = hiltViewModel()
) {

    //'collectAsState' converts flow into compose state to make my UI observe and recompose when
    // data changes.
    val alarms by viewModel.alarms.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopBar(
                title = "Must find matching cards to turn off the alarm :)",
                onHomeClick = {}
            )
        },
        bottomBar = {
            BottomBar(
                onAddAlarmClick = onAddAlarmClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            //If alarm is empty showing different ui box
            if (alarms.isEmpty()) {
                EmptyAlarmList()

            } else {
                //This screen is for displaying alarm list.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(alarms) { alarm ->
                        AlarmItem(
                            hour            = alarm.hour,
                            minute          = alarm.minute,
                            isEnabled       = alarm.isEnabled,
                            label           = alarm.label,
                            onToggleEnabled = { viewModel.toggleAlarmEnabled(alarm) },
                            onAlarmClick    = { onEditAlarmClick(alarm.id) }
                        )
                    }
                }
            }
        }
    }
}