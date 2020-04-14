package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.amadeus.resources.FlightOffer
import com.amadeus.resources.FlightOfferSearch
import com.example.tripin.model.Flight

@Dao
interface FlightDao {

    @Query("select * from allflights")
    suspend fun getFlights() : List<Flight>

    @Insert
    suspend fun addFlight(flight: Flight)

    @Query("delete from allflights")
    suspend fun deleteFlights()



}