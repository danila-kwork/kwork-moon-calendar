package ru.mooncalendar.common

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import ru.mooncalendar.data.pedometer.Day
import ru.mooncalendar.data.pedometer.MainDatabase
import java.util.*

class DateChangedBroadcastReceiver : BroadcastReceiver() {
    private var receiverJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + receiverJob)

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (Intent.ACTION_DATE_CHANGED == action) {
            val datasource = MainDatabase.getInstance(context)
            @SuppressLint("SimpleDateFormat")
            val newDate = DateFormat.standardFormat(Calendar.getInstance().time)
            launchIO(uiScope) {
                val lastDate = datasource.dayDao.getLatest()
                datasource.dayDao.insert(
                    Day(
                        0,
                        newDate,
                        0,
                        lastDate.goal_id,
                        lastDate.goal_name,
                        lastDate.goal_steps
                    )
                )
            }
        }
    }
}
