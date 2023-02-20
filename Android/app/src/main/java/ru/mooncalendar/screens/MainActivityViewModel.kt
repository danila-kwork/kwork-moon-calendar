package ru.mooncalendar.screens

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.mooncalendar.common.DateChangedBroadcastReceiver
import ru.mooncalendar.common.ObservablePreferences
import ru.mooncalendar.common.StepsSensorService

class MainActivityViewModel(
    application: Application
): AndroidViewModel(application) {

    private val preferences: ObservablePreferences
    private var dateChangedBroadcastReceiver = DateChangedBroadcastReceiver()

    private val automaticStepCountingObserver = Observer { isEnabled: Boolean ->
        when(isEnabled) {
            true -> application.startService(Intent(application, StepsSensorService::class.java))
            false -> application.stopService(Intent(application, StepsSensorService::class.java))
        }
    }

    init {
        preferences = ObservablePreferences(application)
        preferences.automaticStepCounting.observeForever(automaticStepCountingObserver)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= 33) {
                application.registerReceiver(dateChangedBroadcastReceiver, IntentFilter(Intent.ACTION_DATE_CHANGED), Context.RECEIVER_EXPORTED)
            }
        }else {
            application.registerReceiver(dateChangedBroadcastReceiver, IntentFilter(Intent.ACTION_DATE_CHANGED))
        }
    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                return MainActivityViewModel(
                    application
                ) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onCleared() {
        getApplication<Application>().unregisterReceiver(dateChangedBroadcastReceiver)
        preferences.automaticStepCounting.removeObserver(automaticStepCountingObserver)
        preferences.destroy()
    }
}
