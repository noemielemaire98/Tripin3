package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.Hotel


@Dao
interface HotelDao {

    @Query("select * from allhotels")
    suspend fun getHotels() : List<Hotel>

    @Insert
    suspend fun addHotel(hotel: Hotel)

    @Query("delete from allhotels")
    suspend fun deleteHotels()

    @Query ("delete from allhotels where id=:id")
    suspend fun deleteHotel(id : Int)

    @Query("select * from allhotels where id=:id")
    suspend fun getHotel(id: Int) : Hotel

}