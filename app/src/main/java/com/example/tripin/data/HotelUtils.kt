package com.example.tripin.data

import androidx.appcompat.app.AppCompatActivity
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory



fun AppCompatActivity.retrofitHotel(): Retrofit {
    // logging toutes les requetes http vont etre dans le logcat
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val activity = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addNetworkInterceptor(StethoInterceptor())
        .build()

    return  Retrofit.Builder()
        .baseUrl("https://hotels4.p.rapidapi.com/")
        // Moshi transfere le Json en objet et inversement equivalent a Gson
        .addConverterFactory(MoshiConverterFactory.create())
        .client(activity)
        .build()
}