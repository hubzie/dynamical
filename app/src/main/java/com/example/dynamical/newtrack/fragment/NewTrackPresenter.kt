package com.example.dynamical.newtrack.fragment

import android.content.Intent
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.mesure.Tracker
import com.example.dynamical.newtrack.service.TrackerService
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline

class NewTrackPresenter(
    private val view: NewTrackView,
    private val application: DynamicalApplication
) {
    private val tracker: Tracker = application.tracker

    fun initialize() {
        // Some random stuff happens here, so objects are required instead of lambdas
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        tracker.time.observe(view.lifecycleOwner, object : Observer<Long> {
            override fun onChanged(time : Long) = view.setTime(Tracker.timeToString(time))
        })
        tracker.stepCount.observe(view.lifecycleOwner, object : Observer<Int> {
            override fun onChanged(stepCount: Int) = view.setStepCount("$stepCount")
        })
        tracker.location.observe(view.lifecycleOwner, object : Observer<Location> {
            override fun onChanged(location: Location) = view.setLocation(location)
        })
        tracker.distance.observe(view.lifecycleOwner, object : Observer<Float> {
            override fun onChanged(distance: Float) =
                view.setDistance(Tracker.distanceToString(distance))
        })
        tracker.route.observe(view.lifecycleOwner, object : Observer<List<LatLng>> {
            override fun onChanged(route: List<LatLng>) { polyline?.points = route }
        })
        tracker.observableState.observe(view.lifecycleOwner, object : Observer<Tracker.State> {
            override fun onChanged(state: Tracker.State) {
                when (state) {
                    Tracker.State.RUNNING -> view.onMeasureStart()
                    Tracker.State.PAUSED -> view.onMeasurePause()
                    Tracker.State.STOPPED -> view.onMeasureReset()
                }
            }
        })

        for (route in tracker.wholeRoute)
            view.getNewPolyline().points = route
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
                    application.applicationContext.getString(R.string.permission_denied_toast),
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