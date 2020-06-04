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


            val destination : List<String> = listOf("Exotique", "Au soleil", "Pour skier", "Pour découvrir", "A la plage", "Pour visiter")
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
                var dest = "Au soleil"
                if(it.name == "Paris" || it.name == "Amsterdam" || it.name == "Rome" || it.name == "New York" || it.name == "Londres" || it.name == "Chicago" || it.name == "Denver" || it.name == "Seattle" || it.name == "Shanghai" || it.name == "Singapour" || it.name == "Bangkok" || it.name == "Washington DC"){
                    dest = "Pour visiter"
                }
                if(it.name == "La Grand île - Hawaii" || it.name == "Honolulu" || it.name == "Oahu" || it.name == "Buenos Aires" || it.name == "Abu dhabi" || it.name == "Medina" || it.name == "Denver" || it.name == "Seattle" || it.name == "Chiang Mai"){
                    dest = "Exotique"
                }
                if(it.name == "Marrakech" || it.name == "Lisbonne" || it.name == "Tampa Bay" || it.name == "Tusayan" || it.name == "Livingstone" || it.name == "Rio de Janeiro" || it.name == "Bilbao" || it.name == "Sydney" || it.name == "Las Vegas"){
                    dest = "Au soleil"
                }
                if(it.name == "La Grand île - Hawaii" || it.name == "Honolulu" || it.name == "Oahu" || it.name == "San Francisco" || it.name == "Venise" || it.name == "Bordeaux" || it.name == "Denver" || it.name == "Lima" || it.name == "Cancun"){
                    dest = "A la plage"
                }
                if(it.name == "Moscow" || it.name == "Saint-Pétersbourg" || it.name == "Göteborg" || it.name == "Malmö" || it.name == "Stockholm" || it.name == "Reykjavik" ){
                    dest = "Pour skier"
                }
                if(it.name == "Tusayan" || it.name == "Sedona" || it.name == "Mantoue" || it.name == "Budapest" || it.name == "Istanbul" || it.name == "Cracovie" ){
                    dest = "Pour découvrir"
                }

                val row = City(it.id, it.name, dest,it.show_in_popular, iataCode, it.cover_image_url)
//                list.add(row)
                Log.d("epf", "$row")
                citydao?.addCity(row)
            }

//            writeToFile(list, this@SplashActivity)
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
