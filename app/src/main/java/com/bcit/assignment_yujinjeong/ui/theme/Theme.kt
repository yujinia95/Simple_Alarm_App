package com.bcit.assignment_yujinjeong.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun WakeUpAlarmTheme(

    //Content is returning compose ui elem.
    content: @Composable () -> Unit
) {

    // Configure transparent status bar
    val view = LocalView.current

    //Below code will be executed, if View is rendered.
    if (!view.isInEditMode) {

        //'SideEffect' making sure code inside runs after composition.
        SideEffect {

            //Getting Activity's window obj(Controlling app's visual frame).
            val window = (view.context as Activity).window

            //This line gives me full screen canvas. (Draw behind sys bars).
            WindowCompat.setDecorFitsSystemWindows(window, false)

            //Making status bar transparent, so app content showing through it.
            @Suppress("DEPRECATION")
            window.setStatusBarColor(Color.Transparent.toArgb())

            //Setting status bar icons to be dark. Gotta make it against light backgrounds!!.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    //Applying design them to my app with colour scheme and typography. content renders UI elem.
    MaterialTheme(
        colorScheme = Colours,
        typography = Typography,
        content = content
    )
}