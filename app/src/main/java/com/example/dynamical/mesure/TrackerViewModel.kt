package com.example.dynamical.mesure

import androidx.lifecycle.ViewModel

// This is needed to make the app work properly in background
// Based on https://github.com/android/location-samples
class TrackerViewModel(val tracker: Tracker) : ViewModel()