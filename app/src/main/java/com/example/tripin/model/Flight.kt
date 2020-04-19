package com.example.tripin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allflights")
data class Flight(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val travelId: Int,
    val SegmentId: Int,
    val prixTotal: Double,
    val dateDepart: String,
    val dureeVol: String,
    val oneway: Boolean,
    val nbEscales: Int,
    val retour: Int
) {


    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..20).map {
            Flight(it, it, it, 200 + it.toDouble(), "DepartureDate$it", "DureeVol$it", true, 2, 0)
        }.toMutableList()

    }
}