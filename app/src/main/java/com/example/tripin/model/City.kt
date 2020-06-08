package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allCities")
data class City(
    @PrimaryKey
    val id: Int,
    var name: String?,
    val category: String,
    val show_in_popular: Boolean?,
    val iataCode: String,
    val cover_image_url:String?
)


