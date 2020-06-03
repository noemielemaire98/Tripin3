package com.example.tripin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "allCities")
data class City(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val category: String,
    val show_in_popular: Boolean?,
    val iataCode: String,
    val cover_image_url:String?
)


