package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "allhotels")
data class Hotel (
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val hotelId: Int,
    val hotelName: String,
    val rate: Int)

{ companion object {
        val all = (1..20).map {
            Hotel(it, it, "Name$it", it) }.toMutableList()
    }
}


