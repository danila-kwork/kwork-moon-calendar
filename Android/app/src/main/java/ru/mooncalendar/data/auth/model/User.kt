package ru.mooncalendar.data.auth.model

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import com.google.firebase.database.DataSnapshot
import ru.mooncalendar.common.extension.toDate
import ru.mooncalendar.common.extension.toLocalDate
import java.time.LocalDate
import java.util.*

data class User(
    val id: String,
    val email: String,
    val password: String,
    val premium: Boolean = false,
    val premiumDate: String? = null,
    val birthday: String
){
    @SuppressLint("NewApi")
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @SuppressLint("NewApi")
    fun isSubscription(): Boolean {
        val date = LocalDate.now()

        return premium && premiumDate != null
                && date <= simpleDateFormat.parse(premiumDate).toLocalDate().plusMonths(1)
    }

    @SuppressLint("NewApi")
    fun debitingFundsDate(): Date {
        val date = simpleDateFormat.parse(premiumDate).toLocalDate()
        return date.plusMonths(1).toDate()
    }
}

fun DataSnapshot.mapUser(): User {
    return User(
        id = this.child("id").value.toString(),
        email = this.child("email").value.toString(),
        password = this.child("password").value.toString(),
        premium = this.child("premium").value.toString().toBoolean(),
        premiumDate = this.child("premiumDate").value.toString(),
        birthday = this.child("birthday").value.toString(),
    )
}