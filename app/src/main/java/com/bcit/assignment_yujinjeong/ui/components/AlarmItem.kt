package com.bcit.assignment_yujinjeong.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Displaying alarm card.
 * There is one alarm per card.
 * Displaying time, text and toggling button for enabling or disabling.
 */
@Composable
fun AlarmItem(
    hour: Int,
    minute: Int,
    isEnabled: Boolean,
    label: String = "",

    //Callback for manipulating alarm related buttons(Enabling alarm, or clicking alarm)
    onToggleEnabled: () -> Unit,
    onAlarmClick: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onAlarmClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically, //Placing element center vertically.
            horizontalArrangement = Arrangement.SpaceBetween //Spacing between the elements.
        ) {
            Column {
                Text (
                    text = String.format("%02d:%02d", hour, minute),
                    style = MaterialTheme.typography.headlineMedium
                )
                //Below, this block will triggered in case text is not empty.
                if (label.isNotEmpty()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            //Switch is for enabling or disabling the alarm.
            Switch(
                checked = isEnabled,
                onCheckedChange = {onToggleEnabled()}
            )
        }
    }
}

/**
 * This method is for in case there is no alarm exist. (Default alarm app screen possibly).
 */
@Composable
fun EmptyAlarmList() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center //Centering box both vertically and horizontally.
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //For informing user that alarm is empty.
            Text(
                text = "No alarms set!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            //Adding some space between Two text
            Spacer(modifier = Modifier.height(16.dp))

            //For informing user to add some alarms.
            Text(
                text = "Add an alarm using the + button :)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}