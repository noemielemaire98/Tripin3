package com.example.tripin.ui.find
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.facebook.stetho.okhttp3.StethoInterceptor

// je rajoute la fonction clientDao à la classe AppcompactACtivity : retourne le DAO

fun AppCompatActivity.dao() : ActivityDao {

    val database: AppDatabase =
        Room.databaseBuilder(this, AppDatabase::class.java,"allactivity").build()
    return database.getActivityDao()

// retourne l'accès à l'API rest


}

fun AppCompatActivity.retrofit():Retrofit {
    // logging toutes les requetes http vont etre dans le logcat
    val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val activity = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addNetworkInterceptor(StethoInterceptor())
        .build()

    return  Retrofit.Builder()
        .baseUrl("https://sandbox.musement.com/")
        // Moshi transfere le Json en objet et inversement equivalent a Gson
        .addConverterFactory(MoshiConverterFactory.create())
        .client(activity)
        .build()
}
