package com.example.dynamical.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("select * from route_table")
    fun allRoutesOnline(): Flow<List<Route>>

    @Query("select * from route_table where id == :id")
    suspend fun getRouteDetails(id: Int): Route

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoute(route: Route)

    @Query("delete from route_table")
    suspend fun clear()
}