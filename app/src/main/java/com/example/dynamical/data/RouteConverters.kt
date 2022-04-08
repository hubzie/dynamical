package com.example.dynamical.data

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class RouteConverters {
    @TypeConverter
    fun fromLatLng(value: LatLng?): String? {
        return value?.let { "${value.latitude}/${value.longitude}" }
    }

    @TypeConverter
    fun toLatLng(value: String?): LatLng? {
        return value?.let {
            val cords = it.split("/")
            return LatLng(cords[0].toDouble(), cords[1].toDouble())
        }
    }
}