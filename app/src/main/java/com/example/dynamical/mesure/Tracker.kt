package com.example.dynamical.mesure

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import android.location.Location
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class Tracker private constructor(application: Application) {
    companion object {
        fun timeToString(time: Long): String = DateUtils.formatElapsedTime(time / 1000)
        fun distanceToString(distance: Float): String {
            return if (distance < 1000.0f) "%.0fm".format(distance)
            else "%.1fkm".format(distance / 1000)
        }

        private var INSTANCE: Tracker? = null

        fun getTracker(application: Application): Tracker {
            return INSTANCE ?: synchronized(this) {
                val instance = Tracker(application)
                INSTANCE = instance
                instance
            }
        }
    }

    private val stepCounter =
        StepCounter(application.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    private val stopwatch = Stopwatch()
    private val gps = GPS(LocationServices.getFusedLocationProviderClient(application))

    val stepCount: LiveData<Int?> get() = stepCounter.stepCount
    val time: LiveData<Long> get() = stopwatch.time
    val location: LiveData<Location?> get() = gps.location

    private val _routePart = MutableLiveData<List<LatLng>>()
    val routePart: LiveData<List<LatLng>> = _routePart

    private val _distance = MutableLiveData<Float?>()
    val distance: LiveData<Float?> = _distance

    var route: List<List<LatLng>> = listOf()
        private set

    private var previousLocation: Location? = null

    private val locationObserver = { it: Location? ->
        if(it != null) {
            val location = LatLng(it.latitude, it.longitude)
            _distance.value = (_distance.value ?: 0.0f) + (previousLocation?.distanceTo(it) ?: 0.0f)
            _routePart.value = _routePart.value?.plus(location) ?: listOf(location)
        }

        previousLocation = it
    }

    enum class State {
        STOPPED, RUNNING, PAUSED
    }

    var state: State = State.STOPPED
        private set(value) {
            field = value
            _observableState.value = field
        }

    private val _observableState = MutableLiveData<State>()
    val observableState: LiveData<State> = _observableState

    fun start() {
        if (state != State.RUNNING) {
            state = State.RUNNING
            stopwatch.start()
            stepCounter.start()
            gps.start()
            previousLocation = null
            location.observeForever(locationObserver)
        }
    }

    fun stop() {
        if (state == State.RUNNING) {
            state = State.PAUSED
            stopwatch.stop()
            stepCounter.stop()
            location.removeObserver(locationObserver)

            routePart.value?.let { part ->
                if (part.isNotEmpty())
                    route = route.plus(listOf(part))
            }
            _routePart.value = listOf()
        }
    }

    fun reset() {
        state = State.STOPPED
        stopwatch.reset()
        stepCounter.reset()
        gps.reset()
        location.removeObserver(locationObserver)

        _routePart.value = listOf()
        route = listOf()

        _distance.value = null
        previousLocation = null
    }
}