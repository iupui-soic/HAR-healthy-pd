package com.example.plhi_har.presentation.theme

import androidx.compose.runtime.Composable
//import androidx.wear.compose.material.MaterialTheme
//import androidx.compose.material3.MaterialTheme


//@Composable
//fun PLHI_HARTheme(
//        content: @Composable () -> Unit
//) {
//    /**
//     * Empty theme to customize for your app.
//     * See: https://developer.android.com/jetpack/compose/designsystems/custom
//     */
//    MaterialTheme(
//            content = content
//    )
//}



import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme




private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1EB980),
    secondary = Color(0xFF0377A6),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1EB980),
    secondary = Color(0xFF0377A6),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun PLHI_HARTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
