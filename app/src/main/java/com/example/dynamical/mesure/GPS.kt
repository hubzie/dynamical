package com.example.dynamical.mesure

import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class GPS(application: Application) : LocationCallback() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application.applicationContext)

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        for (location in locationResult.locations)
            _location.value = location
    }

    fun start() {
        val request = LocationRequest.create().apply {
            interval = 5*1000
            fastestInterval = 5*1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            this,
            Looper.getMainLooper()
        )
    }

    fun stop() {
        fusedLocationClient.removeLocationUpdates(this)
    }
}