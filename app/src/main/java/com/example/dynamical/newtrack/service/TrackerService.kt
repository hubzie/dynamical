package com.example.dynamical.newtrack.service

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.navigation.NavDeepLinkBuilder
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.example.dynamical.measure.Tracker
import com.example.dynamical.measure.Tracker.Companion.distanceToString
import com.example.dynamical.measure.Tracker.Companion.getTracker
import com.example.dynamical.measure.Tracker.Companion.timeToString

class TrackerService : LifecycleService() {
    companion object {
        const val ACTION_PAUSE = "com.example.dynamical.service.PAUSE"
        const val ACTION_RESUME = "com.example.dynamical.service.RESUME"
    }

    private lateinit var tracker: Tracker

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private var isRunning = false

    private var time: Long = 0L
    private var stepCount: Int? = null
    private var distance: Float? = null

    private lateinit var pauseAction: NotificationCompat.Action
    private lateinit var resumeAction: NotificationCompat.Action

    class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val tracker: Tracker = getTracker(context.applicationContext as Application)
            when (intent.action) {
                ACTION_PAUSE -> tracker.stop()
                ACTION_RESUME -> tracker.start()
            }
        }
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val actionIntent = Intent(this, Receiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            this,
            0,
            actionIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun changeAction(action: String) {
        notificationBuilder
            .clearActions()
            .addAction(if(action == ACTION_RESUME) resumeAction else pauseAction)
    }

    private fun updateNotification() {
        val text = getString(R.string.time_label, timeToString(time)) +
                (stepCount?.let{ "; " + getString(R.string.step_count_label, it.toString()) } ?: "") +
                (distance?.let{ "; " + getString(R.string.distance_label, distanceToString(it)) } ?: "")
        val notification = createNotification(text)
        notificationManager.notify(DynamicalApplication.NOTIFICATION_ID, notification)
    }

    private fun createNotification(text: String): Notification {
        return notificationBuilder
            .setContentText(text)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        tracker = getTracker(application)

        pauseAction = NotificationCompat.Action(
            R.drawable.pause,
            getString(R.string.notification_pause_button),
            createPendingIntent(ACTION_PAUSE)
        )
        resumeAction = NotificationCompat.Action(
            R.drawable.start,
            getString(R.string.notification_resume_button),
            createPendingIntent(ACTION_RESUME)
        )

        val onClickPendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.bottom_menu_nav)
            .setDestination(R.id.new_track_fragment)
            .createPendingIntent()

        notificationBuilder =
            NotificationCompat.Builder(this, DynamicalApplication.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(onClickPendingIntent)
                .setOnlyAlertOnce(true)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(DynamicalApplication.NOTIFICATION_ID, createNotification(""))

        if (!isRunning) { // Prevent multiple starts
            tracker.time.observe(this) { time ->
                val oldTime = this.time
                this.time = time
                // If number of seconds changed
                if(oldTime/1000 != time/1000) updateNotification()
            }
            tracker.stepCount.observe(this) { stepCount -> this.stepCount = stepCount }
            tracker.distance.observe(this) { distance -> this.distance = distance }
            tracker.observableState.observe(this) { state ->
                changeAction(when (state) {
                    Tracker.State.RUNNING -> ACTION_PAUSE
                    else -> ACTION_RESUME
                })
                updateNotification()
            }

            isRunning = true
        }

        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Close notification
        tracker.time.removeObservers(this)
        tracker.stepCount.removeObservers(this)
        tracker.distance.removeObservers(this)
        tracker.observableState.removeObservers(this)
        notificationManager.cancel(DynamicalApplication.NOTIFICATION_ID)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }
}