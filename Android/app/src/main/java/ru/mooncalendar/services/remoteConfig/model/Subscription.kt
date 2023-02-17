package ru.mooncalendar.services.remoteConfig.model

data class Subscription(
    val qiwi_token:String,
    val base_subscription_price: Long,
    val description: String
)
