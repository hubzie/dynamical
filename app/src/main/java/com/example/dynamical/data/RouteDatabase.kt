package com.example.dynamical.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Route::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RouteConverters::class)
abstract class RouteDatabase : RoomDatabase() {
    abstract fun routeDao(): RouteDao

    companion object {
        private var INSTANCE: RouteDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RouteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RouteDatabase::class.java,
                    "route_database"
                ).addCallback(DatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.routeDao())
                }
            }
        }

        suspend fun populateDatabase(routeDao: RouteDao) {
            routeDao.clear()

            listOf(
                Route(0, 1000, LatLng(52.237049, 21.017532)), // Warszawa
                Route(1, 7000, LatLng(50.049683, 19.944544)), // Kraków
                Route(2, 8120, LatLng(51.107883, 17.038538)), // Wrocław
                Route(3, 3030, LatLng(53.117653, 23.125734)), // Białystok
                Route(4, 4040, LatLng(52.409538, 16.931992)), // Poznań
                Route(5, 9999, LatLng(54.372158, 18.638306)), // Gdańsk
                Route(6, 1010, LatLng(50.041187, 21.999121)), // Rzeszów
            ).forEach{ route -> routeDao.insertRoute(route) }
        }
    }
}