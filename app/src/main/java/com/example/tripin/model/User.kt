package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allusers")
data class User (@PrimaryKey val id : Int,
                       val uid : String)