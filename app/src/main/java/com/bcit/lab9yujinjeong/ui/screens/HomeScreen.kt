package com.bcit.lab9yujinjeong.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(

    onAddAlarmClick: () -> Unit,
    onEditAlarmClick: (Int) -> Unit,
    viewModel: AlarmViewModel = hiltViewModel()
) {

}