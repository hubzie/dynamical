package com.example.dynamical.data

import kotlinx.coroutines.flow.Flow

class RouteRepository(private val routeDao: RouteDao) {
    val allRoutesOnline: Flow<List<Route>> = routeDao.allRoutesOnline()
}