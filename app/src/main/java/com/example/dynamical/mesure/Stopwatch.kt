package com.example.dynamical.mesure

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R

class Stopwatch {
    // Clock
    private val clock = object : CountDownTimer(
        Long.MAX_VALUE,
        DynamicalApplication.mResources.getInteger(R.integer.clock_interval).toLong()
    ) {
        override fun onTick(p0: Long) = update()
        override fun onFinish() {}
    }

    // Auxiliary variables
    private var timeBeforeStart: Long = 0
    private var startTime: Long = 0
    private var isRunning = false

    // Time
    private val _time = MutableLiveData<Long>()
    val time: LiveData<Long> = _time

    private fun update() {
        _time.value =
            timeBeforeStart + (if (isRunning) System.currentTimeMillis() - startTime else 0L)
    }

    fun start() {
        clock.start()
        isRunning = true
        startTime = System.currentTimeMillis()
        update()
    }

    fun stop() {
        clock.cancel()
        isRunning = false
        timeBeforeStart += System.currentTimeMillis() - startTime
        update()
    }

    fun reset() {
        isRunning = false
        clock.cancel()
        timeBeforeStart = 0
        update()
    }
}