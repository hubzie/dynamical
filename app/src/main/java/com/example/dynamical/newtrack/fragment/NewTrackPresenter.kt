package com.example.dynamical.newtrack.fragment

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.mesure.Stopwatch
import com.example.dynamical.mesure.Tracker
import com.example.dynamical.newtrack.service.TrackerService

class NewTrackPresenter(private val view: NewTrackView, private val application: DynamicalApplication) {
    // Tracker and it's observers
    private val tracker: Tracker = application.tracker

    fun initialize() {
        tracker.time.observe(view.lifecycleOwner) { time -> view.setTime(Stopwatch.timeToString(time)) }
        tracker.stepCount.observe(view.lifecycleOwner) { stepCount -> view.setStepCount("$stepCount") }

        when(tracker.state) {
            Tracker.State.RUNNING -> view.onMeasureStart()
            Tracker.State.PAUSED -> view.onMeasurePause()
            Tracker.State.STOPPED -> view.onMeasureReset()
        }
    }

    private fun pauseMeasure() {
        tracker.stop()
        view.onMeasurePause()
    }

    private fun startMeasure() {
        // Create notification
        if (tracker.state == Tracker.State.STOPPED) {
            if (!view.locationPermission) view.requestPermission()
            if (!view.locationPermission) {
                Log.d("LOCK", "toast")
                Toast.makeText(
                    application.applicationContext,
                    "Required permissions denied",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            val intent = Intent(application.applicationContext, TrackerService::class.java)
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