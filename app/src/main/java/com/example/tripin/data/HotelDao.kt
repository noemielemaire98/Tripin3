package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.Hotel


@Dao
interface HotelDao {

    @Query("select * from allhotels")
    suspend fun getHotels() : List<Hotel>

    @Insert
    suspend fun addHotel(hotel: Hotel)

}