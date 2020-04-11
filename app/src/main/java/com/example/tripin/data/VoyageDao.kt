package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.Voyage

@Dao
interface VoyageDao {

    @Query("select * from myvoyages")
    suspend fun getVoyage() : List<Voyage>

    @Insert
    suspend fun addVoyage(voyage: Voyage)

    @Delete
    suspend fun deleteVoyage(voyage: Voyage)

    @Query("select * from myvoyages where id = :id")
    suspend fun getVoyage(id: Int) : Voyage



}