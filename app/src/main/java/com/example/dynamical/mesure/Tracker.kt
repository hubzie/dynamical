package com.example.dynamical.mesure

import android.app.Application
import android.content.Context
import android.hardware.SensorManager

class Tracker(application: Application) {
    private val stepCounter = StepCounter(application.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    private val stopwatch = Stopwatch()

    fun addStepCounterObserver(observer: StepCounter.Observer) {
        stepCounter.addObserver(observer)
    }
    fun removeStepCounterObserver(observer: StepCounter.Observer) {
        stepCounter.removeObserver(observer)
    }

    fun addStopwatchObserver(observer: Stopwatch.Observer) {
        stopwatch.addObserver(observer)
    }
    fun removeStopwatchObserver(observer: Stopwatch.Observer) {
        stopwatch.removeObserver(observer)
    }

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