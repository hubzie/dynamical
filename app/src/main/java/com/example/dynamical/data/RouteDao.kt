package com.example.dynamical.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("select * from route_table")
    fun allRoutesOnline(): Flow<List<Route>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoute(route: Route)

    @Query("delete from route_table")
    suspend fun clear()
}