package com.example.dynamical.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "route_table")
data class Route(
    @PrimaryKey val id: Int,
    val distance: Int,
    @TypeConverters(RouteConverters::class)
    val position: LatLng
)
