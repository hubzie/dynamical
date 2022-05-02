package com.example.dynamical.mesure

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng

class Tracker(application: Application) {
    private val stepCounter =
        StepCounter(application.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    private val stopwatch = Stopwatch(application)
    private val gps = GPS(application)

    val stepCount: LiveData<Int> get() = stepCounter.stepCount
    val time: LiveData<Long> get() = stopwatch.time
    val location: LiveData<Location> get() = gps.location

    private val _route = MutableLiveData<List<LatLng>>()
    val route: LiveData<List<LatLng>> = _route

    private val _distance = MutableLiveData<Float>()
    val distance: LiveData<Float> = _distance

    var wholeRoute: List<List<LatLng>> = listOf()
        private set

    private var previousLocation: Location? = null

    private val locationObserver = { it: Location ->
        val location = LatLng(it.latitude, it.longitude)
        _distance.value = (_distance.value ?: 0.0f) + (previousLocation?.distanceTo(it) ?: 0.0f)
        previousLocation = it
        _route.value = _route.value?.plus(location) ?: listOf(location)
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
        gps.start()
        location.observeForever(locationObserver)
    }

    fun stop() {
        state = State.PAUSED
        stopwatch.stop()
        stepCounter.stop()
        location.removeObserver(locationObserver)

        route.value?.let { wholeRoute = wholeRoute.plus(listOf(it)) }
        _route.value = listOf()
    }

    fun reset() {
        state = State.STOPPED
        stopwatch.reset()
        stepCounter.reset()
        gps.stop()

        _route.value = listOf()
        wholeRoute = listOf()

        _distance.value = 0.0f
        previousLocation = null
    }
}