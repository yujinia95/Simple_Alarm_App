package com.bcit.lab9yujinjeong.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


// Base colors
val Primary = Color(0xFF8CAAAB)
val PrimaryContainer = Color(0xFFE8CCBF)
val Secondary = Color(0xFF96BDC6)
val SecondaryContainer = Color(0xFFE9D1D6)
val Tertiary = Color(0xFFCFB9A5)
val TertiaryContainer = Color(0xFFE9D6EC)
val OnContent = Color(0xFF81968F)
val Error = Color(0xFFB3261E)
val ErrorContainer = Color(0xFFF9DEDC)
val Background = Color.White

/**
 * Defining colours for app.
 */
val Colours = lightColorScheme(
    primary = Color(0xFF8CAAAB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8CCBF),
    onPrimaryContainer = Color(0xFF81968F),
    secondary = Color(0xFF96BDC6),
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFFE9D1D6),
    onSecondaryContainer = Color(0xFF81968F),
    tertiary = Color(0xFFCFB9A5),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE9D6EC),
    onTertiaryContainer = Color(0xFF81968F),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color.White,
    onBackground = Color(0xFF81968F),
    surface = Color.White,
    onSurface = Color(0xFF81968F)
)

/**
 * Setting up app's theme with colours, typography and my defined UI.
 */
//@Composable
//fun WakeUpAlarmColourTheme(
//
//    //Content is returning compose ui elem.
//    content: @Composable () -> Unit
//) {
//
//    val colourScheme = Colours
//    val view         = LocalView.current    //Getting a current view that has compose ui.
//
//    //Below code will be executed, if View is rendered.
//    if(!view.isInEditMode) {
//
//        //'SideEffect' making sure code inside runs after composition.
//        SideEffect {
//
//            //Getting Activity's window obj(Controlling app's visual frame).
//            val window = (view.context as Activity).window
//
//            //This line gives me full screen canvas. (Draw behind sys bars).
//            WindowCompat.setDecorFitsSystemWindows(window, false)
//
//            //Making status bar transparent, so app content showing through it.
//            @Suppress("DEPRECATION")
//            window.setStatusBarColor(Color.Transparent.toArgb())
//
//            //Setting status bar icons to be dark. Gotta make it against light backgrounds!!.
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
//        }
//    }
//
//    //Applying design them to my app with colour scheme and typography. content renders UI elem.
//    MaterialTheme(
//        colorScheme = colourScheme,
//        typography = Typography,
//        content = content
//    )
//}