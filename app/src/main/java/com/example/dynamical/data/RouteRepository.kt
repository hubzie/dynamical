package com.example.dynamical.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class RouteRepository(private val routeDao: RouteDao) {
    val allRoutesOnline: Flow<List<Route>> = routeDao.allRoutesOnline()

    @WorkerThread
    suspend fun insertRoute(route: Route) = routeDao.insertRoute(route)
    @WorkerThread
    suspend fun deleteRoute(route: Route) = routeDao.deleteRoute(route)
    @WorkerThread
    suspend fun unshare(globalId: String) = routeDao.unshare(globalId)
}