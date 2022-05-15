package com.example.dynamical.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("select * from route_table order by date desc")
    fun allRoutesOnline(): Flow<List<Route>>

    @Query("select * from route_table where id == :id")
    suspend fun getRouteDetails(id: Int): Route

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoute(route: Route)

    @Delete
    suspend fun deleteRoute(route: Route)
}