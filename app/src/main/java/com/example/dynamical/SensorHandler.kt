package com.example.dynamical

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorHandler(private val sensorManager: SensorManager) : SensorEventListener {
    private var action: ((Int) -> Unit)? = null
    private val stepCounterSensor: Sensor =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!

    private var startStep: Int? = null
    private var lastStep: Int = 0
    private var stepsBeforeStart: Int = 0
    val stepCount: Int
        get() = stepsBeforeStart + lastStep - (startStep ?: lastStep)

    fun setOnChangeAction(newAction: (Int) -> Unit) {
        action = newAction
    }

    fun start() {
        startStep = null
        sensorManager.registerListener(
            this,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {
        stepsBeforeStart += lastStep - (startStep ?: lastStep)
        lastStep = (startStep ?: lastStep)
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        startStep = startStep ?: event.values[0].toInt()
        lastStep = event.values[0].toInt()
        action?.invoke(stepCount)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}