package com.example.tripin.ui.find

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface QuotesFlightsService {

    @GET("US/USD/en-US/SFO-sky/JFK-sky/{date}")
    suspend fun listFlights(
        @Path("date") date: String,
        @Header("x-rapidapi-host") apiHost: String,
        @Header("x-rapidapi-key") apiKey: String
    ): ModelAmadeusFlights.Result


    companion object {
        fun create(): QuotesFlightsService {

            val retrofit = Retrofit.Builder()
                .baseUrl("https://test.api.amadeus.com/v2/shopping/flight-offers")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(QuotesFlightsService::class.java)
        }
    }
}