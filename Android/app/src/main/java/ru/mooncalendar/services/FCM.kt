package ru.mooncalendar.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.mooncalendar.MainActivity
import ru.mooncalendar.R
import kotlin.random.Random

class FCM: FirebaseMessagingService() {

    companion object {

        var sharedPref: SharedPreferences? = null

        private const val SHARED_FCM_TOKEN_KEY = "fcm_token"
        private const val CHANNEL_ID = "0"

        private var token:String?
            get(){
                return sharedPref?.getString(SHARED_FCM_TOKEN_KEY,"")
            }
            set(value) {
                sharedPref?.edit()
                    ?.putString(SHARED_FCM_TOKEN_KEY, value)
                    ?.apply()
            }

        fun saveToken(context: Context) {
            sharedPref = context.getSharedPreferences(SHARED_FCM_TOKEN_KEY,Context.MODE_PRIVATE)
            FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener  {
                token = it.token
            }
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    @SuppressLint("NotificationPermission", "InlinedApi")
    override fun onMessageReceived(message: RemoteMessage) {

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        val title = message.notification?.title
        val body = message.notification?.body

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "Notifications"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "Default notifications"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}