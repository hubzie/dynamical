package com.example.dynamical

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import com.example.dynamical.data.RouteDatabase
import com.example.dynamical.data.RouteRepository
import com.example.dynamical.mesure.StepCounter
import com.example.dynamical.mesure.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DynamicalApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { RouteDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RouteRepository(database.routeDao()) }

    val stopwatch: Stopwatch by lazy { Stopwatch() }
    val stepCounter: StepCounter by lazy {
        StepCounter(getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    }
}