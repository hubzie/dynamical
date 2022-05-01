package com.example.dynamical.mesure

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.LiveData

class Tracker(application: Application) {
    private val stepCounter = StepCounter(application.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    private val stopwatch = Stopwatch()
    private val gps = GPS(application)

    val stepCount: LiveData<Int> get() = stepCounter.stepCount
    val time: LiveData<Long> get() = stopwatch.time
    val location: LiveData<Location> get() = gps.location

    enum class State {
        STOPPED, RUNNING, PAUSED
    }

    var state: State = State.STOPPED
        private set

    fun start() {
        state = State.RUNNING
        stopwatch.start()
        stepCounter.start()
        gps.start()
    }

    fun stop() {
        state = State.PAUSED
        stopwatch.stop()
        stepCounter.stop()
        gps.stop()
    }

    fun reset() {
        state = State.STOPPED
        stopwatch.reset()
        stepCounter.reset()
    }
}