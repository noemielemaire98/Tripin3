package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allpreferences")
data class Preference (@PrimaryKey val id : String,
                  val ville : String,
                  val destination : String,
                  val budget : Int)