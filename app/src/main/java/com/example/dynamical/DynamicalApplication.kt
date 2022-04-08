package com.example.dynamical

import android.app.Application
import com.example.dynamical.data.RouteDatabase
import com.example.dynamical.data.RouteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DynamicalApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { RouteDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RouteRepository(database.routeDao()) }
}