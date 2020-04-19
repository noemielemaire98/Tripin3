package com.example.tripin.ui.find

import androidx.appcompat.app.AppCompatActivity
import com.example.tripin.ui.find.QuotesFlightsService
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
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
