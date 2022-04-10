package com.example.dynamical.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class RouteViewModel(private val routeRepository: RouteRepository) : ViewModel() {
    val allRoutesOnline: LiveData<List<Route>> = routeRepository.allRoutesOnline.asLiveData()
    suspend fun getRouteDetails(id: Int): Route = routeRepository.getRouteDetails(id)
}