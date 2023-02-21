package ru.mooncalendar.common

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import ru.mooncalendar.data.database.pedometer.DayDao
import ru.mooncalendar.data.database.MainDatabase


class StepsSensorService : Service(), SensorEventListener {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var steps = 0

    private lateinit var dayDao: DayDao
    private lateinit var sensorManager: SensorManager

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        dayDao = MainDatabase.getInstance(this).dayDao
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent) {
        val eventSteps = event.values[0].toInt()
        if (steps == 0) {
            steps = eventSteps
        } else {
            val deltaSteps = eventSteps - steps
            steps = eventSteps
            launchIO(uiScope) {
                dayDao.addLatestSteps(deltaSteps)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        viewModelJob.cancel()
    }
}