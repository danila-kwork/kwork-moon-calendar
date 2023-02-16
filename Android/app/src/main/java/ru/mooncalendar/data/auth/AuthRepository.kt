package ru.mooncalendar.data.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.data.auth.model.User

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
        onSuccess:() -> Unit = {},
        onFailure:(message:String) -> Unit = {}
    ){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                createUser(
                    id = Firebase.auth.uid!!,
                    email = email,
                    password = password,
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
            .addOnFailureListener { onFailure(it.message ?: "error") }
    }

    private fun createUser(
        id: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        db.reference.child("users").child(id).setValue(User(
            id = id,
            email = email,
            password = password
        ))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "error") }
    }
}