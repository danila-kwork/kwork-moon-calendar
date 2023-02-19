package ru.mooncalendar.data.qiwi.model

data class InvoicingBody(
    val billId:String,
    val amount: Amount,
    val phone: String,
    val email: String,
    val expirationDateTime: String,
    val payUrl:String
)

data class InvoicingResponse(
    val siteId:Int,
    val billId:String,
    val amount: Amount,
    val status: Status
)

data class Amount(
    val currency:String = "RUB",
    val value: Long
)

data class Status(
    val value: String,
    val changedDateTime: String
)