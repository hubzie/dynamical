package com.example.dynamical.mesure

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class GPS(private val fusedLocationProviderClient: FusedLocationProviderClient) {
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private inner class Callback : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations)
                _location.value = location
        }
    }

    private val callback = Callback()

    @SuppressLint("MissingPermission")
    fun start() {
        val request = LocationRequest.create().apply {
            interval = DynamicalApplication.mResources.getInteger(R.integer.GPS_interval).toLong()
            fastestInterval = DynamicalApplication.mResources.getInteger(R.integer.GPS_fastest_interval).toLong()
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationProviderClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }

    fun reset() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
        _location.value = null
    }
}