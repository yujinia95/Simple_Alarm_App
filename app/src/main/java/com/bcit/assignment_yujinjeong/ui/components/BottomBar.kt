package com.bcit.assignment_yujinjeong.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Displaying bottom app bar for adding new alarm.
 */
@Composable
fun BottomBar(
    //Callback for adding alarm button.
    onAddAlarmClick: () -> Unit
) {
    BottomAppBar(
        actions = {},
        //Round floating button (For adding alarm)
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAlarmClick,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Alarm"
                )
            }
        }
    )
}