package com.example.dynamical

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorHandler(private val sensorManager: SensorManager) : SensorEventListener {
    private var action: ((Float) -> Unit)? = null
    private val stepCounterSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!
    private var firstStep: Float? = null
    fun setOnChangeAction(newAction: (Float) -> Unit) {
        action = newAction
    }

    fun start() {
        sensorManager.registerListener(
            this,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        firstStep = firstStep ?: event.values[0]
        action?.invoke(event.values[0]-firstStep!!)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}