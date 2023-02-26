package ru.mooncalendar.data.auth

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.data.auth.model.User
import ru.mooncalendar.data.auth.model.mapUser
import java.util.*

class AuthRepository {

    private val auth = Firebase.auth
    private val db = Firebase.database

    fun auth(
        email: String,
        password: String,
        onSuccess:() -> Unit = {},
        onFailure:(message:String) -> Unit = {}

    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "error") }
    }

    fun reg(
        email: String,
        password: String,
        birthday: String,
        onSuccess:() -> Unit = {},
        onFailure:(message:String) -> Unit = {}
    ){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                createUser(
                    id = Firebase.auth.uid!!,
                    email = email,
                    birthday = birthday,
                    password = password,
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
            .addOnFailureListener { onFailure(it.message ?: "error") }
    }

    @SuppressLint("NewApi")
    fun subscription(
        onSuccess:() -> Unit,
        onFailure:(message:String) -> Unit = {}
    ) {
        val user = Firebase.auth.currentUser ?: return
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = simpleDateFormat.format(Date())

        db.reference.child("users").child(user.uid).child("premium")
            .setValue(true)
            .addOnSuccessListener {
                db.reference.child("users").child(user.uid).child("premiumDate")
                    .setValue(date)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { onFailure(it.message ?: "Error") }
            }
            .addOnFailureListener { onFailure(it.message ?: "Error") }
    }

    @SuppressLint("NewApi")
    fun getUser(
        onSuccess:(User) -> Unit,
        onFailure:(message:String) -> Unit = {}
    ) {
        val user = Firebase.auth.currentUser ?: return

        db.reference.child("users").child(user.uid).get()
            .addOnSuccessListener {
                try {
                    onSuccess(it.mapUser())
                }catch (e:Exception){
                    onFailure(e.message ?: "error")
                }
            }
            .addOnFailureListener {
                onFailure(it.message ?: "error")
            }
    }

    fun editDateUser(date: String, onSuccess: () -> Unit,){

        val user = Firebase.auth.currentUser ?: return

        db.reference.child("users").child(user.uid).child("birthday")
            .setValue(date)
            .addOnSuccessListener { onSuccess() }
    }

    private fun createUser(
        id: String,
        email: String,
        birthday: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        db.reference.child("users").child(id).setValue(User(
            id = id,
            email = email,
            birthday = birthday,
            password = password
        ))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "error") }
    }
}