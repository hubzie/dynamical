package com.example.dynamical.firebase

import com.example.dynamical.data.Route
import com.example.dynamical.data.RouteConverters
import com.google.firebase.auth.FirebaseUser

data class GlobalRoute (
    val owner: String? = null,
    val ownerName: String? = null,
    val time: Long = 0,
    val stepCount: Int? = null,
    val distance: Float? = null,
    val track: String? = null,
    val date: Long = 0
) {
    companion object {
        fun createGlobalRoute(route: Route, owner: FirebaseUser?): GlobalRoute {
            return GlobalRoute(
                owner = owner?.uid,
                ownerName = owner?.displayName,
                time = route.time,
                stepCount = route.stepCount,
                distance = route.distance,
                track = RouteConverters().fromTrack(route.track),
                date = RouteConverters().fromDate(route.date)
            )
        }

        fun toRoute(route: GlobalRoute, globalId: String): Route {
            return with(route) {
                Route(
                    id = -1,

                    globalId = globalId,
                    owner = owner,
                    ownerName = ownerName,

                    time = time,
                    stepCount = stepCount,
                    distance = distance,
                    track = RouteConverters().toTrack(track),
                    date = RouteConverters().toDate(date)
                )
            }
        }
    }
}