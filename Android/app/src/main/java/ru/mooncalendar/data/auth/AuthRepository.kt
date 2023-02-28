package ru.mooncalendar.data.auth

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ru.mooncalendar.common.extension.parseToDateFormat
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
            .addOnFailureListener {
                when(it){
                    is FirebaseAuthInvalidCredentialsException -> {
                        when(it.message){
                            "The email address is badly formatted." ->
                                onFailure("Адрес электронной почты плохо отформатирован.")
                            "The password is invalid or the user does not have a password." ->
                                onFailure("Пароль неверен.")
                            else -> onFailure(it.message ?: "error")
                        }
                    }
                    is FirebaseAuthInvalidUserException -> {
                        onFailure("Нет записи пользователя, соответствующей этому идентификатору. Возможно, пользователь был удален.")
                    }
                    else -> onFailure(it.message ?: "error")
                }
            }
    }

    @SuppressLint("NewApi")
    fun reg(
        email: String,
        password: String,
        birthday: String,
        onSuccess:() -> Unit = {},
        onFailure:(message:String) -> Unit = {}
    ){
        val fromFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val date = fromFormat.parse(birthday).parseToDateFormat()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                createUser(
                    id = Firebase.auth.uid!!,
                    email = email,
                    birthday = date,
                    password = password,
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
            .addOnFailureListener {
                when(it){
                    is FirebaseAuthInvalidCredentialsException -> {
                        when(it.message){
                            "The email address is badly formatted." ->
                                onFailure("Адрес электронной почты плохо отформатирован.")
                            "The password is invalid or the user does not have a password." ->
                                onFailure("Пароль неверен.")
                            "The given password is invalid. [ Password should be at least 6 characters ]" ->
                                onFailure("Пароль должен состоять не менее чем из 6 символов")
                            else -> onFailure(it.message ?: "error")
                        }
                    }
                    is FirebaseAuthInvalidUserException -> {
                        onFailure("Нет записи пользователя, соответствующей этому идентификатору. Возможно, пользователь был удален.")
                    }
                    is FirebaseAuthWeakPasswordException -> {
                        onFailure("Пароль должен состоять не менее чем из 6 символов")
                    }
                    is FirebaseAuthUserCollisionException -> {
                        onFailure("Адрес электронной почты уже используется другой учетной записью")
                    }
                    else -> onFailure(it.message ?: "error")
                }
            }
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

    @SuppressLint("NewApi")
    fun editDateUser(date: String, onSuccess: () -> Unit,){

        val user = Firebase.auth.currentUser ?: return

        val fromFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val dateCorrect = fromFormat.parse(date).parseToDateFormat()

        db.reference.child("users").child(user.uid).child("birthday")
            .setValue(dateCorrect)
            .addOnSuccessListener { onSuccess() }
    }

    fun passwordReset(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ){
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                onFailure(it.message ?: "Error")
                Log.e("addOnFailureListener", it.toString())
            }
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