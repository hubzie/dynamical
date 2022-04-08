package com.example.dynamical.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RouteViewModelFactory(private val repository: RouteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RouteViewModel(repository) as T
        } else
            throw IllegalAccessException()
    }
}