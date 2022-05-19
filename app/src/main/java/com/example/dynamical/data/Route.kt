package com.example.dynamical.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "route_table")
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    var globalId: String? = null,
    var owner: String? = null,
    var ownerName: String? = null,

    val time: Long,
    val stepCount: Int?,
    val distance: Float?,
    @TypeConverters(RouteConverters::class)
    val track: List<List<LatLng>>?,
    @TypeConverters(RouteConverters::class)
    val date: Date
) : Parcelable
