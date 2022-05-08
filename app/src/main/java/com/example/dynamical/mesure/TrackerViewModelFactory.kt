package com.example.dynamical.mesure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TrackerViewModelFactory(private val tracker: Tracker) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(tracker) as T
        } else
            throw IllegalAccessException()
    }
}