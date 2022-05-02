package com.example.dynamical.newtrack.fragment

import android.content.Intent
import android.widget.Toast
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.mesure.Stopwatch
import com.example.dynamical.mesure.Tracker
import com.example.dynamical.newtrack.service.TrackerService
import com.google.android.gms.maps.model.Polyline

class NewTrackPresenter(
    private val view: NewTrackView,
    private val application: DynamicalApplication
) {
    // Tracker and it's observers
    private val tracker: Tracker = application.tracker

    fun initialize() {
        tracker.time.observe(view.lifecycleOwner) { time -> view.setTime(Stopwatch.timeToString(time)) }
        tracker.stepCount.observe(view.lifecycleOwner) { stepCount -> view.setStepCount("$stepCount") }
        tracker.location.observe(view.lifecycleOwner) { location -> view.setLocation(location) }
        tracker.distance.observe(view.lifecycleOwner) { distance ->
            if (distance < 1000.0f) view.setDistance("%.0fm".format(distance))
            else view.setDistance("%.1fkm".format(distance / 1000))
        }
        tracker.route.observe(view.lifecycleOwner) { route -> polyline?.points = route }

        when (tracker.state) {
            Tracker.State.RUNNING -> view.onMeasureStart()
            Tracker.State.PAUSED -> view.onMeasurePause()
            Tracker.State.STOPPED -> view.onMeasureReset()
        }
    }

    private var polyline: Polyline? = null

    private fun pauseMeasure() {
        polyline = null
        tracker.stop()
        view.onMeasurePause()
    }

    private fun startMeasure() {
        // Create notification
        if (tracker.state == Tracker.State.STOPPED) {
            if (!view.locationPermission) view.requestPermission()
            if (!view.locationPermission) {
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

        polyline = view.getNewPolyline()
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

        polyline = null
        tracker.reset()
        view.onMeasureReset()
    }
}