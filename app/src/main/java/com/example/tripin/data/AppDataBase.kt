package com.example.tripin.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tripin.model.Activity
import com.example.tripin.model.Flight
import com.example.tripin.model.LocationList
import com.example.tripin.model.Voyage


@Database(entities = [Voyage::class, Flight::class, Activity::class, LocationList::class], version = 1)

abstract class AppDatabase : RoomDatabase() {

    abstract fun getVoyageDao(): VoyageDao

    abstract fun getFlightDao(): FlightDao

    abstract fun getActivityDao(): ActivityDao

    abstract fun getLocationDao(): LocationDao

}
