package com.example.dynamical.newtrack.fragment

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.LatLng

interface NewTrackView {
    val lifecycleOwner: LifecycleOwner

    val locationPermission: Boolean
    fun requestPermission()

    fun setTime(time: String)
    fun setStepCount(stepCount: String)
    fun setLocation(location: Location)
    fun setDistance(distance: String)
    fun drawRoute(points: List<LatLng>)

    fun onMeasureStart()
    fun onMeasurePause()
    fun onMeasureReset()
}