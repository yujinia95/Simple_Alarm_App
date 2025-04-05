package com.bcit.lab9yujinjeong.ui.theme
//
//import android.app.Activity
//import android.os.Build
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.darkColorScheme
//import androidx.compose.material3.dynamicDarkColorScheme
//import androidx.compose.material3.dynamicLightColorScheme
//import androidx.compose.material3.lightColorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.LocalContext
//
//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)
//
//@Composable
//fun Lab9YujinJeongTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun Lab9YujinJeongTheme(

    //Content is returning compose ui elem.
    content: @Composable () -> Unit
) {
    // Always use light color scheme
    val colorScheme = Colours

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
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}