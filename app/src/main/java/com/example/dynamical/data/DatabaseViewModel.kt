package com.example.dynamical.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DatabaseViewModel(private val routeRepository: RouteRepository) : ViewModel() {
    val allRoutesOnline: LiveData<List<Route>> = routeRepository.allRoutesOnline.asLiveData()
    suspend fun getRouteDetails(id: Int): Route = routeRepository.getRouteDetails(id)
    fun insertRoute(route: Route) = viewModelScope.launch {
        routeRepository.insertRoute(route)
    }
    fun deleteRoute(route: Route) = viewModelScope.launch {
        routeRepository.deleteRoute(route)
    }
}