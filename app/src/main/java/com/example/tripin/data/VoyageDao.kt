package com.example.tripin.data

import androidx.room.*
import com.example.tripin.model.Activity
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

    @Query("select * from myVoyages where titre = :titre")
    suspend fun getVoyageByTitre(titre: String) : Voyage

//    @Query("update myvoyages set titre where id = :id")
    @Update
    suspend fun updateVoyage(voyage: Voyage)

    @Query("update myVoyages set list_activity= :list where titre = :dossier_voyage")
    suspend fun updateVoyageActivities(list: List<Activity>?,dossier_voyage: String)

    @Query("delete from myVoyages where titre = :titre")
    suspend fun deleteFindVoyage(titre: String)



}