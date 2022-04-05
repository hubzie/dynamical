package com.example.dynamical

import android.os.CountDownTimer
import android.text.format.DateUtils

class Stopwatch {
    companion object {
        const val PERIOD = 1000L

        fun timeToString(time: Long): String = DateUtils.formatElapsedTime(time/1000)
    }

    private var action: ((Long) -> Unit)? = null
    private val timer = object : CountDownTimer(Long.MAX_VALUE, PERIOD) {
        override fun onTick(p0: Long) { action?.invoke(time) }
        override fun onFinish() {}
    }
    private var timeBeforeStart: Long = 0
    private var startTime: Long = 0
    val time: Long
        get() = timeBeforeStart + System.currentTimeMillis()-startTime

    fun setOnTickAction(newAction: (Long) -> Unit) {
        action = newAction
    }

    fun start() {
        timer.start()
        startTime = System.currentTimeMillis()
    }

    fun stop() {
        timer.cancel()
        timeBeforeStart += System.currentTimeMillis()-startTime
    }

    fun reset() {
        timeBeforeStart = 0
        timer.cancel()
    }
}