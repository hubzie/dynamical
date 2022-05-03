package com.example.dynamical.data

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RouteConverters {
    @TypeConverter
    fun fromTrack(value: List<List<LatLng>>?): String? {
        return value?.let { Gson().toJson(value) }
    }

    @TypeConverter
    fun toTrack(value: String?): List<List<LatLng>>? {
        return value?.let { Gson().fromJson(value, object : TypeToken<List<List<LatLng>>>() {}.type)}
    }
}