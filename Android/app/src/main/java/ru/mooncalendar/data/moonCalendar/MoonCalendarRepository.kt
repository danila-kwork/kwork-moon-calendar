package ru.mooncalendar.data.moonCalendar

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.data.moonCalendar.model.MoonCalendar
import ru.mooncalendar.data.moonCalendar.model.mapMoonCalendar
import java.util.*
import kotlin.collections.ArrayList

class MoonCalendarRepository {

    private val db = Firebase.database

    @SuppressLint("NewApi")
    fun getMoonCalendar(
        filterDate: Date,
        number: Int = 0,
        onSuccess:(ArrayList<MoonCalendar>) -> Unit,
        onFailure:(message:String) -> Unit = {}
    ) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val correctionDateFormat = "2023-01-01" //simpleDateFormat.format(filterDate)

        db.reference.child("moon_calendar").child("${correctionDateFormat}_${number}").get()
            .addOnSuccessListener {
                val moonCalendarOne = it.mapMoonCalendar() //.children.map { it.mapMoonCalendar() }
                onSuccess(arrayListOf(moonCalendarOne))
                if(number != 1) {
                    getMoonCalendar(
                        filterDate = filterDate,
                        number = number + 1,
                        onSuccess = { result ->
                            result.add(moonCalendarOne)
                            onSuccess(result)
                        },
                        onFailure = onFailure
                    )
                }
            }
            .addOnFailureListener { onFailure(it.message ?: "error") }
    }
}