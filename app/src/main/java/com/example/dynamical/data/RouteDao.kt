package com.example.dynamical.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("select * from route_table order by date desc")
    fun allRoutesOnline(): Flow<List<Route>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: Route)

    @Delete
    suspend fun deleteRoute(route: Route)

    @Query("update route_table set " +
            "globalId = null," +
            "owner = null," +
            "ownerName = null " +
            "where globalId = :globalId")
    suspend fun unshare(globalId: String)
}