package com.example.dynamical.newtrack.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.navigation.NavDeepLinkBuilder
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.mesure.StepCounter
import com.example.dynamical.mesure.Stopwatch
import com.example.dynamical.mesure.Stopwatch.Companion.timeToString
import com.example.dynamical.mesure.Tracker

class TrackerService : Service() {
    private lateinit var notificationManager: NotificationManager
    private var isRunning = false

    // Observer
    private val observer = object : StepCounter.Observer, Stopwatch.Observer {
        private var stepCount = 0
        private var time = 0L

        override fun onStepCountChanged(stepCount: Int) {
            this.stepCount = stepCount
            update()
        }
        override fun onStopwatchTick(time: Long) {
            this.time = time
            update()
        }

        private fun update() {
            val notification = createNotification("Step count: $stepCount; Time: ${timeToString(time)}")
            notificationManager.notify(DynamicalApplication.NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(text: String): Notification {
        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.bottom_menu_nav)
            .setDestination(R.id.new_track_fragment)
            .createPendingIntent()

        return Notification.Builder(this, DynamicalApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setContentText(text)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(DynamicalApplication.NOTIFICATION_ID, createNotification(""))

        if (!isRunning) { // Prevent multiple starts
            val tracker: Tracker = (application as DynamicalApplication).tracker
            tracker.addStepCounterObserver(observer)
            tracker.addStopwatchObserver(observer)

            isRunning = true
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        val tracker: Tracker = (application as DynamicalApplication).tracker
        tracker.removeStepCounterObserver(observer)
        tracker.removeStepCounterObserver(observer)

        notificationManager.cancel(DynamicalApplication.NOTIFICATION_ID)
    }

    override fun onBind(intent: Intent): IBinder? = null
}