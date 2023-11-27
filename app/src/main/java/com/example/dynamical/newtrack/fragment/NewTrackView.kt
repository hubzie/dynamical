package com.example.dynamical.newtrack.fragment

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import com.example.dynamical.data.DatabaseViewModel
import com.example.dynamical.maps.PolylineType
import com.google.android.gms.maps.model.Polyline

interface NewTrackView {
    val databaseViewModel: DatabaseViewModel
    val lifecycleOwner: LifecycleOwner

    val locationPermission: Boolean
    val notificationPermission: Boolean
    val activityPermission: Boolean
    fun requestPermission(callback: () -> Unit)

    fun setTime(time: String)
    fun setStepCount(stepCount: String)
    fun setLocation(location: Location?)
    fun setDistance(distance: String)
    fun getNewPolyline(type: PolylineType): Polyline

    fun hideTime()
    fun hideStepCount()
    fun hideDistance()

    fun onMeasureStart()
    fun onMeasurePause()
    fun onMeasureReset()
}
