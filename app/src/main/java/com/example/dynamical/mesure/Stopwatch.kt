package com.example.dynamical.mesure

import android.os.CountDownTimer
import android.text.format.DateUtils

class Stopwatch {
    companion object {
        const val PERIOD = 100L
        fun timeToString(time: Long): String = DateUtils.formatElapsedTime(time / 1000)
    }

    // Observer pattern
    interface Observer {
        fun onStopwatchTick(time: Long)
    }

    private val observers = ArrayList<Observer>()
    fun addObserver(o: Observer) {
        observers.add(o)
        o.onStopwatchTick(time)
    }
    fun removeObserver(o: Observer) { observers.remove(o) }

    private fun updateObservers() = observers.forEach { o -> o.onStopwatchTick(time) }

    // Clock
    private val clock = object : CountDownTimer(Long.MAX_VALUE, PERIOD) {
        override fun onTick(p0: Long) = updateObservers()
        override fun onFinish() {}
    }

    // Auxiliary variables
    private var timeBeforeStart: Long = 0
    private var startTime: Long = 0
    private var isRunning = false

    // Time
    private val time: Long
        get() = timeBeforeStart + (if(isRunning) System.currentTimeMillis() - startTime else 0L)

    fun start() {
        clock.start()
        isRunning = true
        startTime = System.currentTimeMillis()
        updateObservers()
    }

    fun stop() {
        clock.cancel()
        isRunning = false
        timeBeforeStart += System.currentTimeMillis() - startTime
        updateObservers()
    }

    fun reset() {
        timeBeforeStart = 0
        clock.cancel()
        updateObservers()
    }
}