package com.bcit.assignment_yujinjeong.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import com.bcit.assignment_yujinjeong.di.alarmViewModel
import com.bcit.assignment_yujinjeong.viewmodel.AlarmViewModel
import com.bcit.assignment_yujinjeong.ui.components.AlarmItem
import com.bcit.assignment_yujinjeong.ui.components.BottomBar
import com.bcit.assignment_yujinjeong.ui.components.EmptyAlarmList
import com.bcit.assignment_yujinjeong.ui.components.TopBar

/**
 * Creating screen for Home.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddAlarmClick: () -> Unit,
    onEditAlarmClick: (Int) -> Unit,

    viewModel: AlarmViewModel = alarmViewModel()
) {
    //'collectAsState' converts flow into compose state to make my UI observe and recompose when
    // data changes.
    val alarms by viewModel.alarms.collectAsState(initial = emptyList())
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopBar(
                title = "Welcome to Better Wake Up Now :)",
            )
        },
        bottomBar = {
            BottomBar(
                onAddAlarmClick = onAddAlarmClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Content below the button
            Box(modifier = Modifier.weight(1f)) {
                if (alarms.isEmpty()) {
                    EmptyAlarmList()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(alarms) { alarm ->
                            AlarmItem(
                                hour = alarm.hour,
                                minute = alarm.minute,
                                isEnabled = alarm.isEnabled,
                                label = alarm.label,
                                onToggleEnabled = { viewModel.toggleAlarmEnabled(alarm) },
                                onAlarmClick = { onEditAlarmClick(alarm.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}