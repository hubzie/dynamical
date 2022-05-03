package com.example.dynamical.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "route_table")
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: Long,
    val stepCount: Int?,
    val distance: Float?,
    @TypeConverters(RouteConverters::class)
    val track: List<List<LatLng>>?
)
