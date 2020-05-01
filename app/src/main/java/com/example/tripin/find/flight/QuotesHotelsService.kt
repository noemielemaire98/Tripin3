package com.example.tripin.ui.find

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header

interface QuotesHotelsService {

    suspend fun listHotels(
        @Header("x-rapidapi-host") apiHost: String,
        @Header("x-rapidapi-key") apiKey: String
    ): ModelAmadeusHotels.Result

    companion object{
        fun create(): QuotesHotelsService{
            val retrofit = Retrofit.Builder()
                .baseUrl("https://test.api.amadeus.com/v2/shopping/hotel-offers")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(QuotesHotelsService::class.java)
        }
    }
}