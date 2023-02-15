package ru.mooncalendar.data.moonCalendar

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.data.moonCalendar.model.MoonCalendar
import ru.mooncalendar.data.moonCalendar.model.mapMoonCalendar

class MoonCalendarRepository {

    private val db = Firebase.database

    fun getMoonCalendar(
        filterDate: String? = null,
        onSuccess:(List<MoonCalendar>) -> Unit,
        onFailure:(message:String) -> Unit = {}
    ) {
        db.reference.child("moon_calendar").get()
            .addOnSuccessListener {
                val moonCalendar = it.children.map { it.mapMoonCalendar() }
                    .filter { calendar ->
                        if(filterDate != null)
                            filterDate == calendar.date
                        else
                            true
                    }

                onSuccess(moonCalendar)
            }
            .addOnFailureListener { onFailure(it.message ?: "error") }
    }
}