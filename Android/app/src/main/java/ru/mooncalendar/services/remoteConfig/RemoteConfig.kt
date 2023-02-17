package ru.mooncalendar.services.remoteConfig

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import ru.mooncalendar.services.remoteConfig.model.Subscription

class RemoteConfig {

    private val settings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(43200)
        .build()

    private val remoteConfig = Firebase.remoteConfig.apply {
        setConfigSettingsAsync(settings)
    }

    fun getSubscription(): Subscription? {
        return try {
            remoteConfig.fetchAndActivate()

            val appVersionJson = remoteConfig.getValue("subscription").asString()

            if(appVersionJson.isNotEmpty())
                Gson().fromJson(appVersionJson, Subscription::class.java)
            else
                null
        }catch (e: Exception){
            null
        }
    }
}