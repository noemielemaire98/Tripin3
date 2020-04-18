package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationList(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val name: String
) {


    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..20).map {
            LocationList(it, it.toString())
        }.toMutableList()

    }
}