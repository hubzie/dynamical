package com.example.dynamical.firebase

import com.example.dynamical.data.Route
import com.example.dynamical.firebase.GlobalRoute.Companion.createGlobalRoute
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseDatabase {
    companion object {
        private const val COLLECTION_NAME = "route_table"

        fun shareRoute(route: Route, callback: (String, GlobalRoute) -> Unit) {
            if (Firebase.auth.currentUser == null)
                throw AnonymousSessionException()

            val db = Firebase.firestore
            val globalRoute = createGlobalRoute(route, Firebase.auth.currentUser)
            db.collection(COLLECTION_NAME)
                .add(globalRoute)
                .addOnSuccessListener { callback(it.id, globalRoute)}
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