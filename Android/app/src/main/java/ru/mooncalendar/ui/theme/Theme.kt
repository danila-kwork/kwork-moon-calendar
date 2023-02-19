package ru.mooncalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun MoonCalendarTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val darkColorPalette = darkColors(
        primary = Purple200,
        primaryVariant = Teal200,
        secondary = Teal200
    )

    val lightColorPalette = lightColors(
        primary = Purple500,
        primaryVariant = Teal200,
        secondary = Teal200
    )

    val colors = if (darkTheme) {
        darkColorPalette
    } else {
        lightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}