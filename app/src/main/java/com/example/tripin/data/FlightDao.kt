package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.Flight

@Dao
interface FlightDao {

    @Query("select * from allFlights")
    suspend fun getFlights() : MutableList<Flight>

    @Insert
    suspend fun addFlight(flight: Flight)

    @Query("delete from allFlights")
    suspend fun deleteFlights()

    @Query("delete from allFlights where uuid = :uuid")
    suspend fun deleteFlight(uuid : String)

}