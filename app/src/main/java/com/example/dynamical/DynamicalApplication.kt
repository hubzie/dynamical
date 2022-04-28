package com.example.dynamical

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.dynamical.data.RouteDatabase
import com.example.dynamical.data.RouteRepository
import com.example.dynamical.mesure.Tracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DynamicalApplication : Application() {
    // Constants
    companion object {
        const val NOTIFICATION_ID = 1835 // Some random number
        const val NOTIFICATION_CHANNEL_ID = "Dynamical_notification_channel"
    }

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { RouteDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RouteRepository(database.routeDao()) }

    val tracker by lazy { Tracker(this) }

    override fun onCreate() {
        super.onCreate()

        // Create notification channel
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Dynamical notification channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }
}