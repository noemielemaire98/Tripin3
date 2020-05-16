    package com.example.tripin.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tripin.model.*


    @Database(entities = [Voyage::class, Flight::class, Activity::class,Hotel::class, City::class, Offer::class], version = 1)
@TypeConverters(Converters :: class)


abstract class AppDatabase : RoomDatabase() {

        abstract fun getVoyageDao(): VoyageDao

        abstract fun getFlightDao(): FlightDao

        abstract fun getActivityDao(): ActivityDao

        abstract fun getHotelDao(): HotelDao

        abstract fun getCityDao(): CityDao

        abstract fun getOfferDao() : OfferDao


}
