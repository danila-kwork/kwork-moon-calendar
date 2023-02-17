package ru.mooncalendar.data.moonCalendar.model

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.ui.graphics.Color
import com.google.firebase.database.DataSnapshot
import ru.mooncalendar.common.extension.toLocalDate
import ru.mooncalendar.ui.theme.tintColor
import java.util.*

data class MoonCalendar(
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val moonImageUrl: String = "",
    val table: List<Table> = emptyList()
){
    @SuppressLint("NewApi")
    fun moonCalendarColor(): Color {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = simpleDateFormat.parse(this.date).toLocalDate()

        var year = date.year
        var yearSum = 0

        while(year > 0){
            yearSum += year % 10
            year /=10
        }

        var month = date.month.value
        var monthSum = 0

        while(month > 0){
            monthSum += month % 10
            month /=10
        }

        var day = date.dayOfMonth
        var daySum = 0

        while(day > 0){
            daySum += day % 10
            day /=10
        }

        var sum = yearSum + monthSum + daySum
        var number = 0

        while(sum > 0){
            number += sum % 10
            sum /=10
        }

        return when(number) {
            1 -> Color.Yellow
            2 -> Color.Gray
            3 -> Color(0xFFFF6F00)
            4 -> Color(0xFF39352A)
            5 -> Color.Green
            6 -> Color(0xFFE6A8D7)
            7 -> Color(0xFFE0B0FF)
            8 -> {
                if((0..1).random() == 0){
                    Color.Black
                }else {
                    Color(0xFF002137)
                }
            }
            9 -> Color.Red
            else -> tintColor
        }
    }
}

data class Table(
    val parameter: String = "",
    val value: String = ""
)

fun DataSnapshot.mapMoonCalendar(): MoonCalendar? {

    try {
        val table = this.child("table").value.toString().split(";").toTypedArray()

        return MoonCalendar(
            date = this.child("date").value.toString(),
            title = this.child("title").value.toString(),
            description = this.child("desc").value.toString(),
            moonImageUrl = this.child("moon_image_url").value.toString(),
            table = table.map {

                val column = it.split("-").toTypedArray()

                Table(
                    parameter = column[0],
                    value = column[1]
                )
            }
        )
    }catch (e:Exception){
        return null
    }
}