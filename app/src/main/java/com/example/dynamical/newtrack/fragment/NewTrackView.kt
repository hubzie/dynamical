package com.example.dynamical.newtrack.fragment

import androidx.lifecycle.LifecycleOwner

interface NewTrackView {
    val lifecycleOwner: LifecycleOwner
    fun setTime(time: String)
    fun setStepCount(stepCount: String)
    fun onMeasureStart()
    fun onMeasurePause()
    fun onMeasureReset()
}