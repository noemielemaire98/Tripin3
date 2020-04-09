package com.example.tripin.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.tripin.model.Voyage


@Database(entities = arrayOf(Voyage::class),version = 1)

abstract class AppDatabase : RoomDatabase(){

    abstract fun getVoyageDao() : VoyageDao
}
