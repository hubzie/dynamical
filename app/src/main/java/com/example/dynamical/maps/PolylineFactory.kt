package com.example.dynamical.maps

import androidx.core.content.res.ResourcesCompat
import com.example.dynamical.DynamicalApplication
import com.example.dynamical.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

class PolylineFactory {
    companion object {
        fun createPolyline(map: GoogleMap): Polyline {
            val resources = DynamicalApplication.mResources
            return map.addPolyline(PolylineOptions()).apply {
                startCap = RoundCap()
                endCap = RoundCap()
                jointType = JointType.ROUND
                width = resources.getDimension(R.dimen.line_width)
                color = ResourcesCompat.getColor(resources, R.color.purple_500, null)
            }
        }
    }
}