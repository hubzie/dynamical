package com.example.dynamical

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import com.example.dynamical.data.Route
import com.example.dynamical.data.RouteDatabase
import com.example.dynamical.data.RouteRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DynamicalApplication : Application() {
    // Constants
    companion object {
        const val NOTIFICATION_ID = 1835 // Some random number
        const val NOTIFICATION_CHANNEL_ID = "Dynamical_notification_channel"
        lateinit var mResources: Resources

        const val SHARED_PREFERENCES_NAME = "Dynamical_shared_preferences"
        const val FOLLOWED_ROUTE = "FOLLOWED_ROUTE"
    }

    // Maintain current activity
    private inner class ApplicationActivityLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) { currentActivity = activity }
        override fun onActivityPaused(activity: Activity) { currentActivity = null }
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    var currentActivity: Activity? = null
        private set

    // Database
    private val database by lazy { RouteDatabase.getDatabase(this) }
    val repository by lazy { RouteRepository(database.routeDao()) }

    // Storing current route
    private val sharedPreferences by lazy { getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)!! }
    var followedRoute: Route?
        set(value) {
            sharedPreferences.edit().apply {
                value?.let { putString(FOLLOWED_ROUTE, Gson().toJson(it)) }
                    ?: remove(FOLLOWED_ROUTE)
                apply()
            }
        }
        get() = sharedPreferences.getString(FOLLOWED_ROUTE, null)?.let {
                Gson().fromJson(it, object : TypeToken<Route>() {}.type) as Route
            }

    // Setup
    override fun onCreate() {
        super.onCreate()
        mResources = resources

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

        // Maintain currentActivity
        registerActivityLifecycleCallbacks(ApplicationActivityLifecycleCallback())
    }
}