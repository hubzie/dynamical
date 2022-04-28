package com.example.dynamical.newtrack.fragment

interface NewTrackView {
    fun setTime(time: String)
    fun setStepCount(stepCount: String)
    fun onMeasureStart()
    fun onMeasurePause()
    fun onMeasureReset()
}