package com.example.dynamical.mesure

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import androidx.lifecycle.LiveData

class Tracker(application: Application) {
    private val stepCounter = StepCounter(application.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    private val stopwatch = Stopwatch()

    val stepCount: LiveData<Int> get() = stepCounter.stepCount
    val time: LiveData<Long> get() = stopwatch.time

    enum class State {
        STOPPED, RUNNING, PAUSED
    }

    var state: State = State.STOPPED
        private set

    fun start() {
        state = State.RUNNING
        stopwatch.start()
        stepCounter.start()
    }

    fun stop() {
        state = State.PAUSED
        stopwatch.stop()
        stepCounter.stop()
    }

    fun reset() {
        state = State.STOPPED
        stopwatch.reset()
        stepCounter.reset()
    }
}