package com.example.tripin.find.hotel

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HotelAPI {

    @GET ("location/search")
    suspend fun getLocation (
        @Query("query") city : String,
        @Header("x-rapidapi-host") host : String,
        @Header("x-rapidapi-key") key : String
    ):ModelRapid.Rapid
}