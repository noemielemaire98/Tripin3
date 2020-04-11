package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allflights")
data class Flight (@PrimaryKey(autoGenerate = true) val QuoteId: Int,
                   val MinPrice: Double, val DepartureDate: String, val Direct: Boolean) {


    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..20).map{
            Flight(it, 200 + it.toDouble(), "DepartureDate$it", true)
        }.toMutableList()

    }
}