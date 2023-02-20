package ru.mooncalendar.common

import java.util.*

fun getCurrentDate(): Date {
    val calendar = Calendar.getInstance()
    return calendar.time
}