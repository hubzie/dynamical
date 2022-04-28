package com.example.dynamical.mesure

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounter(private val sensorManager: SensorManager) : SensorEventListener {
    // Observer pattern
    interface Observer {
        fun onStepCountChanged(stepCount: Int)
    }

    private val observers = ArrayList<Observer>()
    fun addObserver(o: Observer) {
        observers.add(o)
        o.onStepCountChanged(stepCount)
    }
    fun removeObserver(o: Observer) { observers.remove(o) }

    private fun updateObservers() = observers.forEach { o -> o.onStepCountChanged(stepCount) }

    // Configure sensor
    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    // Auxiliary variables
    private var startStep: Int? = null
    private var lastStep: Int = 0
    private var stepsBeforeStart: Int = 0

    // Step count
    private val stepCount: Int
        get() = stepsBeforeStart + lastStep - (startStep ?: lastStep)

    // Start step counter
    fun start() {
        startStep = null
        sensorManager.registerListener(
            this,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        updateObservers()
    }

    // Stop step counter
    fun stop() {
        stepsBeforeStart += lastStep - (startStep ?: lastStep)
        lastStep = (startStep ?: lastStep)
        sensorManager.unregisterListener(this)
        updateObservers()
    }

    // Reset step counter
    fun reset() {
        stop()
        stepsBeforeStart = 0
        updateObservers()
    }

    // Handle sensor
    override fun onSensorChanged(event: SensorEvent) {
        startStep = startStep ?: event.values[0].toInt()
        lastStep = event.values[0].toInt()
        updateObservers()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}