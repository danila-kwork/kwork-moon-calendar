package ru.mooncalendar.data.auth.model

data class User(
    val id:String,
    val email:String,
    val password:String,
    val premium: Boolean = false
)