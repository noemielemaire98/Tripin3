package com.example.tripin.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tripin.model.Converters
import com.example.tripin.model.Flight
import com.example.tripin.model.Hotel
import com.example.tripin.model.Voyage


@Database(entities = [Voyage::class, Flight::class, Hotel::class], version = 1)
@TypeConverters(Converters :: class)

abstract class AppDatabase : RoomDatabase() {

    abstract fun getVoyageDao(): VoyageDao

    abstract fun getFlightDao(): FlightDao

    abstract fun getHotelDao(): HotelDao

}
