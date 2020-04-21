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
    val heureDepart: String,
    val dateArrivee: String,
    val heureArrivee: String,
    val dureeVol: String,
    val lieuDepart: String,
    val lieuArrivee: String,
    val carrierCode: String,
    val carrierCodeLogo: String,
    val carrierName: String,
    val nbEscales: Int,
    val retour: Int
) : Serializable {


    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..20).map {
            Flight(
                it,
                it,
                it,
                200 + it.toDouble(),
                100 + it.toDouble(),
                "DepartureDate$it",
                "DepartureHour$it",
                "ArrivalDate$it",
                "ArrivalHour$it",
                "DureeVol$it",
                "CDG",
                "SYD",
                "AFR",
                "AF",
                "Air France",
                2,
                0
            )
        }.toMutableList()

    }
}