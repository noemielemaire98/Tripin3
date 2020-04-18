package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.LocationList

@Dao
interface LocationDao {

    @Query("select * from locations")
    suspend fun getLocation() : List<LocationList>

    @Insert
    suspend fun addLocation(locationList: LocationList)

    @Query("delete from locations")
    suspend fun deleteLocation()
}