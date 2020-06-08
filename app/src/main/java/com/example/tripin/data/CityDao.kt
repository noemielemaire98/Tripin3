package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.City

@Dao
interface CityDao {

    @Query("select * from allCities")
    suspend fun getCity() : List<City>

    @Query("delete from allCities")
    suspend fun deleteCities()

    @Insert
    suspend fun addCity(city: City)

    @Query("select * from allCities where name = :name")
    suspend fun getCity(name: String) : City

    @Query("select * from allCities where category = :category")
    suspend fun getCityByDestination(category: String) : List<City>

}