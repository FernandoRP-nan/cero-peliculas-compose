package com.example.cero.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Clay,
    secondary = WarmStone,
    tertiary = Moss,
    background = NightLeather,
    surface = Color(0xFF624332),
    onPrimary = Ink,
    onSecondary = Ink,
    onTertiary = Mist,
    onBackground = Mist,
    onSurface = Mist
)

private val LightColorScheme = lightColorScheme(
    primary = Leather,
    secondary = Clay,
    tertiary = Moss,
    background = Sand,
    surface = Mist,
    onPrimary = Mist,
    onSecondary = Ink,
    onTertiary = Mist,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun CeroTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
