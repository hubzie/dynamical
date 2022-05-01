package com.example.dynamical.newtrack.fragment

import android.location.Location
import androidx.lifecycle.LifecycleOwner

interface NewTrackView {
    val lifecycleOwner: LifecycleOwner

    val locationPermission: Boolean
    fun requestPermission()

    fun setTime(time: String)
    fun setStepCount(stepCount: String)
    fun setLocation(location: Location)

    fun onMeasureStart()
    fun onMeasurePause()
    fun onMeasureReset()
}