package com.example.tripin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allflights")
data class Flight (@PrimaryKey(autoGenerate = true) val id: Int = 1,
                   val grandTotal: Double, val dateDepart: String, val oneway: Boolean) {


    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..20).map{
            Flight(it, 200 + it.toDouble(), "DepartureDate$it", true)
        }.toMutableList()

    }
}