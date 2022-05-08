package com.example.dynamical.mesure

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class GPS(private val application: DynamicalApplication) {
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private inner class GPSLocationCallback : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations)
                _location.value = location
        }
    }

    private val locationCallback = GPSLocationCallback()

    private fun getRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = DynamicalApplication.mResources.getInteger(R.integer.GPS_interval).toLong()
            fastestInterval = DynamicalApplication.mResources.getInteger(R.integer.GPS_fastest_interval).toLong()
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun askForTurningOnGPS(): Task<LocationSettingsResponse> {
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(getRequest())
            .build()

        return LocationServices.getSettingsClient(application).checkLocationSettings(settingsRequest)
    }

    @SuppressLint("MissingPermission")
    fun start() {
        fusedLocationProviderClient.requestLocationUpdates(
            getRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun reset() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        _location.value = null
    }
}