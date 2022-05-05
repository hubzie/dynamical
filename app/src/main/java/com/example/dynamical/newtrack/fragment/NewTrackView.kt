package com.example.dynamical.newtrack.fragment

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import com.example.dynamical.data.RouteViewModel
import com.example.dynamical.maps.PolylineType
import com.google.android.gms.maps.model.Polyline

interface NewTrackView {
    val routeViewModel: RouteViewModel
    val lifecycleOwner: LifecycleOwner

    val locationPermission: Boolean
    fun requestPermission(callback: () -> Unit)

    fun setTime(time: String)
    fun setStepCount(stepCount: String)
    fun setLocation(location: Location)
    fun setDistance(distance: String)
    fun getNewPolyline(type: PolylineType): Polyline

    fun hideStepCount()
    fun hideDistance()

    fun onMeasureStart()
    fun onMeasurePause()
    fun onMeasureReset()
}