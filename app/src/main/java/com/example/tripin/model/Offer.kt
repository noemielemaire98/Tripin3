package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alloffers")
data class Offer (@PrimaryKey val id : String,
    val type : String,
    val nb_bed : Int?,
    val bed_Type : String?,
    val description : String?,
    val price : Double){
}