package com.example.dynamical

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import com.example.dynamical.data.RouteDatabase
import com.example.dynamical.data.RouteRepository
import com.example.dynamical.mesure.StepCounter
import com.example.dynamical.mesure.Stopwatch
import com.example.dynamical.mesure.Tracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DynamicalApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { RouteDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RouteRepository(database.routeDao()) }

    val tracker by lazy { Tracker(this) }
}