package com.example.tripin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "allflights")
data class Flight(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val travelId: Int,
    val SegmentId: Int,
    val prixTotal: Double,
    val prixParAdult: Double,
    val dateDepart: String,
    val dateArrivee: String,
    val dureeVol: String,
    val oneway: Boolean,
    val nbEscales: Int,
    val retour: Int
) : Serializable {


    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..20).map {
            Flight(it, it, it, 200 + it.toDouble(), 100 + it.toDouble(), "DepartureDate$it", "ArrivalDate$it","DureeVol$it", true, 2, 0)
        }.toMutableList()

    }
}