package com.example.tripin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.CityDao
import com.example.tripin.data.retrofit
import com.example.tripin.data.retrofitHotel
import com.example.tripin.find.activity.ActivitybyCity
import com.example.tripin.find.hotel.HotelAPI
import com.example.tripin.model.City
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.runBlocking
import java.io.FileWriter
import java.io.IOException
import java.io.OutputStreamWriter

class SplashActivity : AppCompatActivity() {

    private var citydao: CityDao? = null

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

            val airportCsv = resources.openRawResource(R.raw.iata_city)
            val listAirports: List<Map<String, String>> =
                csvReader().readAllWithHeader(airportCsv)

            val result = service.listcities("fr-FR")
            //           val list: MutableList<City> = mutableListOf()



            var iataCode = ""
            result.map {
                run loop@{
                    listAirports.map { itMap ->
                        itMap.map { itIn ->
                            if (itIn.value == it.name) {
                                iataCode = itMap["city_code"].toString()
                                return@loop
                            }
                        }
                    }
                }
                val row = City(it.id, it.name, it.show_in_popular, iataCode, it.cover_image_url)
//                list.add(row)
                Log.d("epf", "$row")
                citydao?.addCity(row)
            }

//            writeToFile(list, this@SplashActivity)
        }

        runBlocking {
            val service2 = retrofitHotel().create(HotelAPI::class.java)
            val result2 = service2.getLocation("fr_FR","paris","d82ce245cbmsh006f040e3753b19p1d57ddjsna1fe19bfba68")

            result2.suggestions.map {
                if (it.group == "CITY_GROUP"){
                    val entity = it.entities[0] //504261
                }
            }
        }



        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }

    private fun writeToFile(data: List<City>, context: Context) {

        val CSV_HEADER = "name,city_code"
        //      var fileWriter: FileWriter? = null
        val fileWriter =
            OutputStreamWriter(context.openFileOutput("iata_city.csv", Context.MODE_PRIVATE))

        try {
            fileWriter.append(CSV_HEADER)
            fileWriter.append('\n')

            for (customer in data) {
                fileWriter.append(customer.name)
                fileWriter.append(',')
                fileWriter.append(customer.iataCode)
                fileWriter.append('\n')
            }

            println("Write CSV successfully!")
        } catch (e: Exception) {
            println("Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter.flush()
                fileWriter.close()
            } catch (e: IOException) {
                println("Flushing/closing error!")
                e.printStackTrace()
            }
        }
    }
}
