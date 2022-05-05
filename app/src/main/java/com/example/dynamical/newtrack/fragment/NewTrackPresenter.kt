package com.example.dynamical.newtrack.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.data.Route
import com.example.dynamical.maps.PolylineType
import com.example.dynamical.mesure.Tracker
import com.example.dynamical.newtrack.service.TrackerService
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline

class NewTrackPresenter(
    private val view: NewTrackView,
    private val application: DynamicalApplication
) {
    private val tracker = application.tracker

    private var polyline: Polyline? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            tracker.gpsContext = (binder as TrackerService.TrackerBinder).serviceContext
        }

        override fun onServiceDisconnected(className: ComponentName) {
            tracker.gpsContext = null
        }
    }
    private var isBonded = false

    fun initialize() {
        // Some random stuff happens here, so objects are required instead of lambdas
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        tracker.time.observe(view.lifecycleOwner, object : Observer<Long> {
            override fun onChanged(time : Long) = view.setTime(Tracker.timeToString(time))
        })
        tracker.stepCount.observe(view.lifecycleOwner, object : Observer<Int?> {
            override fun onChanged(stepCount: Int?) {
                stepCount?.let { view.setStepCount(it.toString()) }
                    ?: view.hideStepCount()
            }
        })
        tracker.location.observe(view.lifecycleOwner, object : Observer<Location?> {
            override fun onChanged(location: Location?) = view.setLocation(location)
        })
        tracker.distance.observe(view.lifecycleOwner, object : Observer<Float?> {
            override fun onChanged(distance: Float?) {
                distance?.let { view.setDistance(Tracker.distanceToString(it)) }
                    ?: view.hideDistance()
            }
        })
        tracker.routePart.observe(view.lifecycleOwner, object : Observer<List<LatLng>> {
            override fun onChanged(route: List<LatLng>) {
                polyline?.points = route
            }
        })
        tracker.observableState.observe(view.lifecycleOwner, object : Observer<Tracker.State> {
            override fun onChanged(state: Tracker.State) {
                when (state) {
                    Tracker.State.RUNNING -> {
                        polyline = view.getNewPolyline(PolylineType.CURRENT)
                        view.onMeasureStart()
                    }
                    Tracker.State.PAUSED -> view.onMeasurePause()
                    Tracker.State.STOPPED -> view.onMeasureReset()
                }
            }
        })

        for (route in tracker.route)
            view.getNewPolyline(PolylineType.CURRENT).points = route
    }

    private fun pauseMeasure() {
        polyline = null
        tracker.stop()
        view.onMeasurePause()
    }

    private fun startMeasure(askForPermission: Boolean = true) {
        // Create notification
        if (tracker.state == Tracker.State.STOPPED) {
            if (!view.locationPermission) {
                if (askForPermission) view.requestPermission { startMeasure(false) }
                else {
                    Toast.makeText(
                        application.applicationContext,
                        application.applicationContext.getString(R.string.permission_denied_toast),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }

            Intent(application.applicationContext, TrackerService::class.java).also { intent ->
                application.startForegroundService(intent)
                isBonded = application.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }

        polyline = view.getNewPolyline(PolylineType.CURRENT)
        tracker.start()
        view.onMeasureStart()
    }

    fun onFlipState() {
        if (tracker.state == Tracker.State.RUNNING) pauseMeasure()
        else startMeasure()
    }

    fun onEnd() {
        // Delete notification
        if (tracker.state != Tracker.State.STOPPED) {
            if(isBonded) {
                application.unbindService(connection)
                isBonded = false
            }

            Intent(application.applicationContext, TrackerService::class.java).also { intent ->
                application.stopService(intent)
            }
            tracker.stop()

            // Save track
            val route = Route(
                time = tracker.time.value ?: 0L,
                stepCount = tracker.stepCount.value,
                distance = tracker.distance.value,
                track = tracker.route
            )
            view.routeViewModel.insertRoute(route)
            view.setLocation(null)
        }

        polyline = null
        tracker.reset()
        view.onMeasureReset()
    }
}