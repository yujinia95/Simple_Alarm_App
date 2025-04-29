package com.bcit.assignment_yujinjeong

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import android.app.AlarmManager
import com.bcit.assignment_yujinjeong.ui.naviagtion.Nav
import com.bcit.assignment_yujinjeong.ui.naviagtion.Screen
import com.bcit.assignment_yujinjeong.ui.theme.WakeUpAlarmTheme
import android.provider.Settings
import android.util.Log
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect

/**
 * Yujin Jeong, A01310724
 */

/**
 * Entry point to run the alarm app.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestExactAlarmPermission()

        setContent {
            val navController = rememberNavController()

            // Check if we should show game screen
            if (intent.getBooleanExtra("SHOW_GAME", false)) {
                val alarmId = intent.getIntExtra("ALARM_ID", -1)

                //Using LaunchedEffect to navigate after composition
                LaunchedEffect(key1 = Unit) {
                    navController.navigate(Screen.CardGame.createRoute(alarmId))
                }
            }

            WakeUpAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Nav(navController = navController)
                }
            }
        }
    }


    /**
     * Might not using it.
     * Requests permission to schedule exact alarms on Android 12 and above.
     * If permission is not granted, launches the settings screen where the user can enable it.
     */
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:${packageName}")
                )
                exactAlarmLauncher.launch(intent)
            }
        }
    }


    /**
     * Might not using it.
     * Activity result launcher for handling the user's response to the exact alarm permission request.
     * Shows a Toast if the permission was not granted.
     */
    private val exactAlarmLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Handle the result of the permission request
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                Log.d("MainActivity", "Exact alarm permission granted")
            } else {
                Toast.makeText(
                    this,
                    "Alarm permission is required for this app to function correctly",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}