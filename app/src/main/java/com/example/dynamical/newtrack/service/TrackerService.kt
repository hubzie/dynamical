package com.example.dynamical.newtrack.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.navigation.NavDeepLinkBuilder
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.mesure.Stopwatch.Companion.timeToString
import com.example.dynamical.mesure.Tracker

// TODO: prevent sleeping
class TrackerService : LifecycleService() {
    private lateinit var notificationBuilder: Notification.Builder
    private lateinit var notificationManager: NotificationManager
    private var isRunning = false

    private var stepCount = 0
    private var time = 0L

    private fun updateNotification() {
        val notification = createNotification("Step count: $stepCount; Time: ${timeToString(time)}")
        notificationManager.notify(DynamicalApplication.NOTIFICATION_ID, notification)
    }

    private fun createNotification(text: String): Notification {
        return notificationBuilder
            .setContentText(text)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.bottom_menu_nav)
            .setDestination(R.id.new_track_fragment)
            .createPendingIntent()

        notificationBuilder = Notification.Builder(this, DynamicalApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(DynamicalApplication.NOTIFICATION_ID, createNotification(""))

        if (!isRunning) { // Prevent multiple starts
            val tracker: Tracker = (application as DynamicalApplication).tracker
            tracker.time.observe(this) { time ->
                this.time = time
                updateNotification()
            }
            tracker.stepCount.observe(this) { stepCount ->
                this.stepCount = stepCount
                // updateNotification()
            }

            isRunning = true
        }

        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager.cancel(DynamicalApplication.NOTIFICATION_ID)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }
}