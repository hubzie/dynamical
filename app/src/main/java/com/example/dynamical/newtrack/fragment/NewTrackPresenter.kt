package com.example.dynamical.newtrack.fragment

import android.content.Intent
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.mesure.StepCounter
import com.example.dynamical.mesure.Stopwatch
import com.example.dynamical.mesure.Tracker
import com.example.dynamical.newtrack.service.TrackerService

class NewTrackPresenter(private val view: NewTrackView, private val application: DynamicalApplication) {
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

    private fun pauseMeasure() {
        tracker.stop()
        view.onMeasurePause()
    }

    private fun startMeasure() {
        // Create notification
        if (tracker.state == Tracker.State.STOPPED) {
            val intent = Intent(application.applicationContext, TrackerService::class.java)
            application.startForegroundService(intent)
            application.startForegroundService(intent)
        }

        tracker.start()
        view.onMeasureStart()
    }

    fun onFlipState() {
        if (tracker.state == Tracker.State.RUNNING) pauseMeasure()
        else startMeasure()
    }

    fun onReset() {
        // Delete notification
        if (tracker.state != Tracker.State.STOPPED) {
            val intent = Intent(application.applicationContext, TrackerService::class.java)
            application.stopService(intent)
        }

        tracker.reset()
        view.onMeasureReset()
    }
}