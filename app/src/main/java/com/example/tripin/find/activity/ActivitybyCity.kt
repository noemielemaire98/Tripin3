package com.example.tripin.find.activity

import com.example.tripin.find.activity.ModelMusement
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ActivitybyCity {
    @GET("api/v3/activities")
    suspend fun listActivity(
        //@Query("text") city: String,
        @Query("text") city : String,
        @Header("accept-language") lang: String,
        @Header("x-musement-currency") monnaie: String
    ): ModelMusement.Welcome




}
