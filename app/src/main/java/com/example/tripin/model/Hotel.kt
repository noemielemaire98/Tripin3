package com.example.tripin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity (tableName = "allhotels")
data class Hotel (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val hotelId: String,
    val hotelName: String,
    val hotelDescription: String?,
    val rate: Int?,
    val image_url: String?,
    val adresse : String?,
    val email: String,
    val telephone: String,
    var favoris : Boolean) : Parcelable

{ companion object {
        val all = (1..20).map {
            Hotel(it, "Id$it", "Name$it", "Description$it",it, "R.drawable.activite1","adresse$it","email$it", "telephone$it", false) }.toMutableList()
    }
}


