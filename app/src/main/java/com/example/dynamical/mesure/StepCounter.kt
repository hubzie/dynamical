package com.example.dynamical.mesure

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class StepCounter(private val sensorManager: SensorManager) : SensorEventListener {
    // Configure sensor
    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    // Auxiliary variables
    private var startStep: Int? = null
    private var lastStep: Int = 0
    private var stepsBeforeStart: Int? = null

    // Step count
    private val _stepCount = MutableLiveData<Int?>()
    val stepCount: LiveData<Int?> = _stepCount

    private fun update() {
        _stepCount.value = (stepsBeforeStart ?: 0) + lastStep - (startStep ?: lastStep)
        if(_stepCount.value == 0) _stepCount.value = null
    }

    // Start step counter
    fun start() {
        startStep = null
        sensorManager.registerListener(
            this,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        update()
    }

    // Stop step counter
    fun stop() {
        stepsBeforeStart = (stepsBeforeStart ?: 0) + lastStep - (startStep ?: lastStep)
        if (stepsBeforeStart == 0) stepsBeforeStart = null

        lastStep = (startStep ?: lastStep)
        sensorManager.unregisterListener(this)
        update()
    }

    // Reset step counter
    fun reset() {
        sensorManager.unregisterListener(this)
        stepsBeforeStart = null
        startStep = null
        update()
    }

    // Handle sensor
    override fun onSensorChanged(event: SensorEvent) {
        startStep = startStep ?: event.values[0].toInt()
        lastStep = event.values[0].toInt()
        update()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}