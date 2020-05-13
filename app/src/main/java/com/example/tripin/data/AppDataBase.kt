    package com.example.tripin.data

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.tripin.model.*

@Database(entities = [Voyage::class, Flight::class, Activity::class,Hotel::class, City::class], version = 1)
@TypeConverters(Converters::class,ListActivityConverter::class)



abstract class AppDatabase : RoomDatabase() {

    abstract fun getVoyageDao(): VoyageDao

    abstract fun getFlightDao(): FlightDao

    abstract fun getActivityDao(): ActivityDao

    abstract fun getHotelDao(): HotelDao

    abstract fun getCityDao(): CityDao

}


    class ListActivityConverter{
        @TypeConverter fun fromListActivity(listActivity : List<Activity>) : List<String> {
            var arraylist = arrayListOf<String>()
            listActivity.map {
                var string = "${it.uuid};${it.title};${it.cover_image_url};${it.formatted_iso_value};${it.operational_days};${it.reviews_avg};${it.category};${it.url};${it.top_seller};${it.must_see};${it.description};${it.about};${it.latitude};${it.longitude}"
                arraylist.add(string)
            }

            return arraylist.toList()
        }
        @TypeConverter fun toListActivity(liststring : List<String>) : List<Activity>{
            var arraylist = arrayListOf<Activity>()
            liststring.map {
                var items = it.split(";")
                Log.d("RRR","$items")
                var cat = items[6].substring(1,items[6].length-1)
                var categories: List<String> = cat.split(",")
                var activite = Activity(items[0],items[1],items[2],items[3], items[4], items[5].toDouble(), categories, items[7], items[8].toBoolean(), items[9].toBoolean(), items[10], items[11], items[12].toDouble(), items[13].toDouble())

                arraylist.add(activite)
            }
            return arraylist.toList()
        }
    }

