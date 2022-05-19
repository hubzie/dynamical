package com.example.dynamical.firebase

import com.example.dynamical.data.Route
import com.example.dynamical.firebase.GlobalRoute.Companion.createGlobalRoute
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.net.Socket

class FirebaseDatabase {
    companion object {
        private const val COLLECTION_NAME = "route_table"
        private const val TIMEOUT = 1000

        @Suppress("BlockingMethodInNonBlockingContext")
        private suspend fun hasInternetConnectionAsync(): Deferred<Boolean> = withContext(Dispatchers.IO) {
            async {
                val socket = Socket()
                try {
                    socket.connect(InetSocketAddress("8.8.8.8", 53), TIMEOUT)
                    true
                } catch (e: Exception) {
                    false
                } finally {
                    socket.close()
                }
            }
        }

        private fun hasInternetConnection(): Boolean = runBlocking {
            hasInternetConnectionAsync().await()
        }

        fun shareRoute(route: Route, callback: (String, GlobalRoute) -> Unit) {
            if (Firebase.auth.currentUser == null)
                throw AnonymousSessionException()
            if (!hasInternetConnection())
                throw NetworkTimeoutException()

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

        fun unshareRoute(route: Route, callback: () -> Unit) {
            if (!hasInternetConnection())
                throw NetworkTimeoutException()

            val db = Firebase.firestore
            db.collection(COLLECTION_NAME)
                .document(route.globalId!!)
                .delete()
                .addOnSuccessListener { callback()}
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