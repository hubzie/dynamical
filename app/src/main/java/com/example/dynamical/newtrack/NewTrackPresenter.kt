package com.example.dynamical.newtrack

import com.example.dynamical.DynamicalApplication
import com.example.dynamical.mesure.StepCounter
import com.example.dynamical.mesure.Stopwatch

class NewTrackPresenter(private val view: NewTrackView, application: DynamicalApplication) {
    // Stopwatch and it's observer
    private val stopwatch: Stopwatch = application.stopwatch
    private val stopwatchObserver = object : Stopwatch.Observer {
        override fun onStopwatchTick(time: Long) = view.setTime(Stopwatch.timeToString(time))
    }

    // Step counter and it's observer
    private val stepCounter: StepCounter = application.stepCounter
    private val stepCounterObserver = object : StepCounter.Observer {
        override fun onStepCountChanged(stepCount: Int) = view.setStepCount("$stepCount")
    }

    // Is measure active?
    private var isRunning = false

    fun initialize() {
        stopwatch.reset()

        stopwatch.addObserver(stopwatchObserver)
        stepCounter.addObserver(stepCounterObserver)

        isRunning = false
    }

    fun finalize() {
        stopwatch.removeObserver(stopwatchObserver)
        stepCounter.removeObserver(stepCounterObserver)
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