package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "allhotels")
data class Hotel (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val hotelId: String,
    val hotelName: String,
    val hotelDescription: String?,
    val rate: Int?)

{ companion object {
        val all = (1..20).map {
            Hotel(it, "Id$it", "Name$it", "Description$it",it) }.toMutableList()
    }
}


