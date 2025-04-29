package com.bcit.assignment_yujinjeong.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bcit.assignment_yujinjeong.data.dataclass.Alarm
import com.bcit.assignment_yujinjeong.ui.components.TopBar
import java.time.DayOfWeek
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.ui.window.Dialog
import com.bcit.assignment_yujinjeong.di.alarmViewModel
import com.bcit.assignment_yujinjeong.viewmodel.AlarmViewModel


/**
 * Creating screen for adding alarm or editing alarm.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmScreen(
    alarmId: Int? = null,
    //onBackClick and onSaveClick are for navigation callbacks.
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    // Use our custom extension function instead of hiltViewModel()
    viewModel: AlarmViewModel = alarmViewModel()
) {
    //For edit mode alarId must be present
    val isEditMode = alarmId != null

    //Live state of all alarms.
    val allAlarms by viewModel.alarms.collectAsState(initial = emptyList())

    //If user tries to edit, find the existing alarm that matches id, if not found or adding new
    // alarm, create new one (default as 7:00).
    val existingAlarm = if (isEditMode) {
        allAlarms.find { it.id == alarmId } ?: Alarm(hour = 7, minute = 0)
    } else {
        Alarm(hour = 7, minute = 0)
    }

    // Remember states for holding editable alarm values. Compose watches theses and start
    // recomposition if there are changes.
    var hour by remember {mutableStateOf(existingAlarm.hour)}
    var minute by remember { mutableStateOf(existingAlarm.minute)}
    var label by remember { mutableStateOf(existingAlarm.label) }

    var selectedDays by remember { mutableStateOf(existingAlarm.repeatDays) }
    var showTimePicker by remember {mutableStateOf(false)}
    var showDeleteConfirmation by remember {mutableStateOf(false)}

    ///Using timePickerState for track time selection in the timePickerDialog. (Hours range 0 to 24)
    val timePickerState = remember {
        TimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = true
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                title = if(isEditMode) "Edit Alarm" else "Add Alarm",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },

        //Round floating button
        //This button is for updating or adding alarm (saving alarm) button.
        floatingActionButton = {
            SaveAlarmButton(
                isEditMode    = isEditMode,
                existingAlarm = existingAlarm,
                hour          = hour,
                minute        = minute,
                label         = label,
                selectedDays  = selectedDays,
                viewModel     = viewModel,
                onSaveClick   = onSaveClick
            )
        }
    ) { paddingValues ->

        //This block is for content for making new alarm form UI.
        AlarmFormContent(
            paddingValues = paddingValues,
            hour = hour,
            minute = minute,
            label = label,
            selectedDays = selectedDays,
            isEditMode = isEditMode,
            onLabelChange = { label = it },
            onSelectedDaysChange = { selectedDays = it },
            onShowTimePicker = { showTimePicker = true },
            onShowDeleteConfirmation = { showDeleteConfirmation = true }
        )

        //Displaying my custom time picker dialog.
        if (showTimePicker) {
            TimePickerDialog(
                onDismiss = { showTimePicker = false },
                onConfirm = {
                    hour = timePickerState.hour
                    minute = timePickerState.minute
                    showTimePicker = false
                },
                state = timePickerState
            )
        }

        //If user is in edit mode and want to delete the alarm, this block of code will get
        // triggered.
        if (showDeleteConfirmation && isEditMode) {
            DeleteConfirmationDialog(
                onDismiss = { showDeleteConfirmation = false },
                onConfirm = {
                    existingAlarm.let {
                        viewModel.deleteAlarm(it)
                    }
                    showDeleteConfirmation = false
                    //After delete, going bact to previous page.
                    onBackClick()
                }
            )
        }
    }
}

/***
 * This is the button for saving alarm after creating new one or editing existed one.
 */
@Composable
private fun SaveAlarmButton(
    isEditMode: Boolean,
    existingAlarm: Alarm,
    hour: Int,
    minute: Int,
    label: String,
    selectedDays: Set<DayOfWeek>,
    viewModel: AlarmViewModel,
    onSaveClick: () -> Unit
) {
    FloatingActionButton(
        onClick = {
            //This will be triggered only when user is in edit alarm mode.
            if (isEditMode) {
                viewModel.updateAlarm(
                    existingAlarm.copy(
                        hour = hour,
                        minute = minute,
                        label = label,
                        repeatDays = selectedDays
                    )
                )
                //This gets triggered in regular adding alarm mode.
            } else {
                viewModel.addAlarm(
                    hour = hour,
                    minute = minute,
                    label = label,
                    repeatDays = selectedDays
                )
            }
            onSaveClick()
        }
    ) {
        Text("Save")
    }
}


/**
 * This function is helper function and creating form to make alarm.
 */
@Composable
private fun AlarmFormContent(
    paddingValues: PaddingValues,
    hour: Int,
    minute: Int,
    label: String,
    selectedDays: Set<DayOfWeek>,
    isEditMode: Boolean,

    //Below is for event handler. (Callbacks)
    onLabelChange: (String) -> Unit,
    onSelectedDaysChange: (Set<DayOfWeek>) -> Unit,
    onShowTimePicker: () -> Unit,
    onShowDeleteConfirmation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //to display time and time picker.
        TimeDisplayCard(
            hour = hour,
            minute = minute,
            onShowTimePicker = onShowTimePicker
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Label input (User can specify what this alarm for)
        LabelTextField(
            label =label,
            onLabelChange = onLabelChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        //For user selecting which days to repeat
        RepeatDaysSelector(
            selectedDays = selectedDays,
            onSelectedDaysChange = onSelectedDaysChange
        )

        //This delete button only appear when use is in edit mode.
        if(isEditMode) {
            Spacer(
                modifier = Modifier.height(32.dp)
            )
            DeleteAlarmButton(onShowDeleteConfirmation = onShowDeleteConfirmation)
        }
    }
}


/**
 * To display time for user select desired alarm time.
 */
@Composable
private fun TimeDisplayCard(
    hour: Int,
    minute: Int,
    onShowTimePicker: () -> Unit
) {

    Card(
        onClick = onShowTimePicker,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%02d:%02d", hour, minute),
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Tap to change",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}


/**
 * This function is helper function for user can type label.
 */
@Composable
private fun LabelTextField(
    label: String,
    onLabelChange: (String) -> Unit
) {
    OutlinedTextField(
        value = label,
        onValueChange = onLabelChange,
        label = {Text("Label")},
        modifier = Modifier.fillMaxWidth()
    )
}


/**
 * This function is helperr function for toggling day selecting function.
 */
@Composable
private fun DayToggleRow(
    day: DayOfWeek,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            // Displaying days name only 3 letters (e.g. Mon, Tue, Wed)
            text = day.name.substring(0,3),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = isSelected,
            onCheckedChange = onToggle
        )
    }
}


/**
 * This function is helper function for user can select which days to repeat the alarm.
 */
@Composable
private fun RepeatDaysSelector(
    selectedDays: Set<DayOfWeek>,
    onSelectedDaysChange: (Set<DayOfWeek>) -> Unit
) {
    Text(
        text = "Repeat",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )

    //forEach loop going through DayOfWeek enum class value for picking repeat days.
    DayOfWeek.entries.forEach {
            day ->

        DayToggleRow(
            day = day,
            isSelected = selectedDays.contains(day),
            onToggle = {

                //If toggle for chosen day is on, add current day in selectedDays.
                    isSelected ->
                val newSelectedDays = if (isSelected) {
                    selectedDays + day

                    //If toggle for chose day is off, remove the day from selectedDays.
                } else {
                    selectedDays - day
                }
                onSelectedDaysChange(newSelectedDays)
            }
        )
    }
}


/**
 * This function is helper method. For deleting alarm button.
 */
@Composable
private fun DeleteAlarmButton(
    onShowDeleteConfirmation: () -> Unit
) {

    OutlinedButton(
        onClick = onShowDeleteConfirmation,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        ),

        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete"
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text("Delete Alarm")
    }
}

/**
 * This is a help function, to show delete confirmation dialog to user.
 */
@Composable
private fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Alarm")},
        text = {Text("Are you sure you want to delete this alarm?") },

        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },

        //Cancel delete button.
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


/**
 * Helper function for picking time for setting alarm.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    state: TimePickerState
) {

    //Base class for dialogs.
    Dialog(onDismissRequest = onDismiss) {

        //Creating round container for time picker.
        Surface (
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                //This is the actual time picker UI. State param is for controlling current time
                // selection.
                TimePicker(state = state)

                Spacer(modifier = Modifier.height(24.dp))

                //For buttons (cancel or ok)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    //Cancel button
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    //Save time button.
                    Button(onClick = onConfirm) {
                        Text("OK")
                    }
                }
            }
        }
    }
}












