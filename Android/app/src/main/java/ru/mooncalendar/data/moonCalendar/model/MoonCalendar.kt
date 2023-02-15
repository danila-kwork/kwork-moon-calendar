package ru.mooncalendar.data.moonCalendar.model

import com.google.firebase.database.DataSnapshot

data class MoonCalendar(
    val date: String,
    val title: String,
    val description: String,
    val moonImageUrl: String,
    val table: List<Table>
)

data class Table(
    val parameter: String,
    val value: String
)

fun DataSnapshot.mapMoonCalendar(): MoonCalendar {

    val table = this.child("").value.toString().split(";").toTypedArray()

    return MoonCalendar(
        date = this.child("date").value.toString(),
        title = this.child("title").value.toString(),
        description = this.child("description").value.toString(),
        moonImageUrl = this.child("moon_image_url").value.toString(),
        table = table.map {

            val column = it.split("-").toTypedArray()

            Table(
                parameter = column[0],
                value = column[1]
            )
        }
    )
}