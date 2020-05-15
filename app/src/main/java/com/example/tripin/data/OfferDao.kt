package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.Offer


@Dao
interface OfferDao {

    @Query("select * from alloffers")
    suspend fun getOffers() : List<Offer>

    @Query("delete from alloffers")
    suspend fun deleteOffers()

    @Insert
    suspend fun addOffers(offer: Offer)

}