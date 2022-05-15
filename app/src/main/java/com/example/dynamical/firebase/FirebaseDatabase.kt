package com.example.dynamical.firebase

import com.example.dynamical.data.Route
import com.example.dynamical.data.RouteConverters
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseDatabase {
    companion object {
        private const val COLLECTION_NAME = "route_table"

        private data class GlobalRoute(
            val time: Long,
            val stepCount: Int?,
            val distance: Float?,
            val track: String?,
            val date: Long
        )

        private fun toGlobalRoute(route: Route): GlobalRoute {
            return GlobalRoute(
                time = route.time,
                stepCount = route.stepCount,
                distance = route.distance,
                track = RouteConverters().fromTrack(route.track),
                date = RouteConverters().fromDate(route.date)
            )
        }

        fun shareRoute(route: Route, callback: () -> Unit) {
            if (Firebase.auth.currentUser == null)
                throw AnonymousSessionException()

            val db = Firebase.firestore
            db.collection(COLLECTION_NAME)
                .add(toGlobalRoute(route))
                .addOnSuccessListener { callback() }
                .addOnFailureListener {
                    val e = it as FirebaseFirestoreException
                    if (e.code == FirebaseFirestoreException.Code.UNAUTHENTICATED)
                        throw AnonymousSessionException()
                    else if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE)
                        throw NetworkTimeoutException()
                    throw it
                }
        }
    }
}