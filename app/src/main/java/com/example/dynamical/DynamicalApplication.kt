package com.example.dynamical

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import com.example.dynamical.data.RouteDatabase
import com.example.dynamical.data.RouteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DynamicalApplication : Application() {
    // Constants
    companion object {
        const val NOTIFICATION_ID = 1835 // Some random number
        const val NOTIFICATION_CHANNEL_ID = "Dynamical_notification_channel"
        lateinit var mResources: Resources

        const val SHARED_PREFERENCES_NAME = "Dynamical_shared_preferences"
        const val FOLLOWED_ROUTE = "FOLLOWED_ROUTE"
    }

    val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { RouteDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RouteRepository(database.routeDao()) }

    val sharedPreferences by lazy { getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)!! }

    var followedRoute: Int? = null
        set(value) {
            sharedPreferences.edit().apply {
                value?.let { putInt(FOLLOWED_ROUTE, it) }
                    ?: remove(FOLLOWED_ROUTE)
                apply()
            }
            field = value
        }

    override fun onCreate() {
        super.onCreate()
        mResources = resources

        // Load followed route
        followedRoute = if(sharedPreferences.contains(FOLLOWED_ROUTE))
            sharedPreferences.getInt(FOLLOWED_ROUTE, 0)
        else null

        // Create notification channel
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Dynamical notification channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setSound(null, null)
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }
}