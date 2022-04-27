package com.example.dynamical.newtrack

import android.hardware.SensorManager
import com.example.dynamical.mesure.StepCounter
import com.example.dynamical.mesure.Stopwatch

class NewTrackPresenter(private val view: NewTrackView) {
    // Stopwatch and it's observer
    private val stopwatch = Stopwatch()
    private val stopwatchObserver = object : Stopwatch.Observer {
        override fun onStopwatchTick(time: Long) = view.setTime(Stopwatch.timeToString(time))
    }

    // Step counter and it's observer
    private var _stepCounter: StepCounter? = null
    private val stepCounter get() = _stepCounter!!
    private val stepCounterObserver = object : StepCounter.Observer {
        override fun onStepCountChanged(stepCount: Int) = view.setStepCount("$stepCount")
    }

    // Is measure active?
    private var isRunning = false

    fun initialize(sensorManager: SensorManager) {
        _stepCounter = StepCounter(sensorManager)
        stopwatch.reset()

        stopwatch.addObserver(stopwatchObserver)
        stepCounter.addObserver(stepCounterObserver)

        isRunning = false
    }

    fun finalize() {
        stopwatch.removeObserver(stopwatchObserver)
        stepCounter.removeObserver(stepCounterObserver)
        _stepCounter = null
        isRunning = false
    }

    private fun stopMeasure() {
        isRunning = false
        stopwatch.stop()
        stepCounter.stop()
        view.onMeasureStop()
    }

    private fun startMeasure() {
        isRunning = true
        stopwatch.start()
        stepCounter.start()
        view.onMeasureStart()
    }

    fun onButtonClicked() {
        if (isRunning) stopMeasure()
        else startMeasure()
    }
}