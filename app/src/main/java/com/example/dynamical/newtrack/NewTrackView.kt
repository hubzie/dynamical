package com.example.dynamical.newtrack

interface NewTrackView {
    fun setTime(time: String)
    fun setStepCount(stepCount: String)
    fun onMeasureStart()
    fun onMeasureStop()
}