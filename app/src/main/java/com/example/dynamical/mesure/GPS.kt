package com.example.dynamical.mesure

import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dynamical.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class GPS(application: Application) : LocationCallback() {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        for (location in locationResult.locations)
            _location.value = location
    }

    private val request = LocationRequest.create().apply {
        interval = application.resources.getInteger(R.integer.GPS_interval).toLong()
        fastestInterval = application.resources.getInteger(R.integer.GPS_fastest_interval).toLong()
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    fun start() {
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