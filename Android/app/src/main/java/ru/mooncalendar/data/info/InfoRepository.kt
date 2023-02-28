package ru.mooncalendar.data.info

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.common.extension.parseToDateFormat
import java.util.*

data class Info(
    var date: String,
    val info: String
)

fun DataSnapshot.mapInfo(): Info {
    return Info(
        date = child("date").value.toString(),
        info = child("info").value.toString()
    )
}

class InfoRepository {

    private val database = Firebase.database

    fun get(
        date: String,
        onSuccess: (Info?) -> Unit
    ) {
        database.reference.child("info").child(date).get()
            .addOnSuccessListener {
                try {
                    val info = it.mapInfo()
                    if(info.info != "null" && info.date != "null"){
                        onSuccess(it.mapInfo())
                    }else{
                        onSuccess(null)
                    }

                }catch (_:Exception){}
            }
    }

    @SuppressLint("NewApi")
    fun create(info: Info, onSuccess: () -> Unit){

        val fromFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val date = fromFormat.parse(info.date).parseToDateFormat()

        database.reference.child("info").child(date).setValue(info.apply { this.date = date })
            .addOnSuccessListener { onSuccess() }
    }
}