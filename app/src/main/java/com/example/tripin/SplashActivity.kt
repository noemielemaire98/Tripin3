package com.example.tripin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.room.Room
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.CityDao
import com.example.tripin.data.retrofit
import com.example.tripin.find.activity.ActivitybyCity
import com.example.tripin.model.City
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.android.synthetic.main.fragment_find_activity2.*
import kotlinx.coroutines.runBlocking

class SplashActivity : AppCompatActivity() {

    private var citydao : CityDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()

        citydao = database.getCityDao()

        runBlocking {
            citydao?.deleteCities()
            val service = retrofit().create(ActivitybyCity::class.java)
            val result = service.listcities("fr-FR")
            result.map {
                var row = City(it.id,it.name,it.show_in_popular)
                citydao?.addCity(row)
            }
        }

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        },1000)
    }
}
