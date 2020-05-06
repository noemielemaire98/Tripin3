package com.example.tripin.data

import androidx.room.*
import com.example.tripin.model.Voyage

@Dao
interface VoyageDao {

    @Query("select * from myVoyages")
    suspend fun getVoyage() : List<Voyage>

    @Insert
    suspend fun addVoyage(voyage: Voyage)

    @Delete
    suspend fun deleteVoyage(voyage: Voyage)

    @Query("select * from myVoyages where id = :id")
    suspend fun getVoyage(id: Int) : Voyage

    @Update
    suspend fun updateVoyage(voyage: Voyage)





}