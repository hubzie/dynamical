package com.example.dynamical.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DatabaseViewModelFactory(private val repository: RouteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatabaseViewModel(repository) as T
        } else
            throw IllegalAccessException()
    }
}