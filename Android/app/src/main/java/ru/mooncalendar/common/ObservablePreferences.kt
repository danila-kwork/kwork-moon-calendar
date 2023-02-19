package ru.mooncalendar.common

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ObservablePreferences(context: Context) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val _editableGoals = MutableLiveData(preferences.getBoolean("editableGoals", true))
    private val _automaticStepCounting = MutableLiveData(preferences.getBoolean("automaticStepCounting", true))
    private val _notifications = MutableLiveData(preferences.getBoolean("notifications", true))

    val editableGoals: LiveData<Boolean> = _editableGoals
    val automaticStepCounting: LiveData<Boolean> = _automaticStepCounting
    val notifications: LiveData<Boolean> = _notifications

    private val prefListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            "editableGoals" -> {
                _editableGoals.value = sp.getBoolean(key, true)
            }
            "automaticStepCounting" -> {
                _automaticStepCounting.value = sp.getBoolean(key, true)
            }
            "notifications" -> {
                _notifications.value = sp.getBoolean(key, true)
            }
        }
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(prefListener)
    }

    fun destroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}
