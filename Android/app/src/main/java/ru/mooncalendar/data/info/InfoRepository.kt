package ru.mooncalendar.data.info

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class Info(
    val date: String,
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

    fun create(info: Info, onSuccess: () -> Unit){
        database.reference.child("info").child(info.date).setValue(info)
            .addOnSuccessListener { onSuccess() }
    }
}