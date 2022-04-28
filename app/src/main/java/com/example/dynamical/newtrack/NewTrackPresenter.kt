package com.example.dynamical.newtrack

import com.example.dynamical.DynamicalApplication
import com.example.dynamical.mesure.StepCounter
import com.example.dynamical.mesure.Stopwatch
import com.example.dynamical.mesure.Tracker

class NewTrackPresenter(private val view: NewTrackView, application: DynamicalApplication) {
    // Tracker and it's observers
    private val tracker: Tracker = application.tracker
    private val stopwatchObserver = object : Stopwatch.Observer {
        override fun onStopwatchTick(time: Long) = view.setTime(Stopwatch.timeToString(time))
    }
    private val stepCounterObserver = object : StepCounter.Observer {
        override fun onStepCountChanged(stepCount: Int) = view.setStepCount("$stepCount")
    }

    fun initialize() {
        tracker.addStopwatchObserver(stopwatchObserver)
        tracker.addStepCounterObserver(stepCounterObserver)

        when(tracker.state) {
            Tracker.State.RUNNING -> view.onMeasureStart()
            Tracker.State.PAUSED -> view.onMeasurePause()
            Tracker.State.STOPPED -> view.onMeasureReset()
        }
    }

    fun finalize() {
        tracker.removeStopwatchObserver(stopwatchObserver)
        tracker.removeStepCounterObserver(stepCounterObserver)
    }

    private fun stopMeasure() {
        tracker.stop()
        view.onMeasurePause()
    }

    private fun startMeasure() {
        tracker.start()
        view.onMeasureStart()
    }

    fun onFlipState() {
        if (tracker.state == Tracker.State.RUNNING) stopMeasure()
        else startMeasure()
    }

    fun onReset() {
        tracker.reset()
        view.onMeasureReset()
    }
}