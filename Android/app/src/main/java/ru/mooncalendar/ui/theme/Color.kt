package ru.mooncalendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val tintColor = Color(0xFFFF974D)

val Purple200 = tintColor
val Purple500 = tintColor
val Teal200 = tintColor

@Composable
fun primaryBackground(): Color {
    val darkMode = isSystemInDarkTheme()

    return if(darkMode){
        Color(0xFF0F0F0F)
    }else {
        Color(0xFFFFFFFF)
    }
}

@Composable
fun secondaryBackground(): Color {
    val darkMode = isSystemInDarkTheme()

    return if(darkMode){
        Color(0xFF141414)
    }else {
        Color(0xFFF3F4F5)
    }
}

@Composable
fun primaryText(): Color {
    val darkMode = isSystemInDarkTheme()

    return if(darkMode){
        Color.White
    }else {
        Color.Black
    }
}