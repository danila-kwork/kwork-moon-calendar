package ru.mooncalendar.data.qiwi.model

import android.annotation.SuppressLint
import java.time.LocalDateTime
import java.time.ZoneOffset

@SuppressLint("NewApi")
fun getInvoicingDate(): String {
    return LocalDateTime.now().plusYears(1).atZone(ZoneOffset.UTC).toString()
}

data class InvoicingBody(
    val amount: Amount,
    val expirationDateTime:String = getInvoicingDate(),
    val customer: Customer? = null,
    val customFields: CustomFields = CustomFields()
)

data class InvoicingResponse(
    val siteId:String,
    val billId:String,
    val status: Status,
    val customer: Customer,
    val payUrl: String
)

data class Amount(
    val currency:String = "RUB",
    val value: Float
)

data class Customer(
    val email:String
)

data class CustomFields(
    val paySourcesFilter:String = "qw,card,mobile"
)

data class Status(
    val value: QiwiStatus,
    val changedDateTime: String
)

enum class QiwiStatus {
    WAITING,
    PAID,
    REJECTED,
    EXPIRED
}