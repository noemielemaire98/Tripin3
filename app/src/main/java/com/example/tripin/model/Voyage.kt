package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "myvoyages")
data class Voyage(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val titre: String,
    val date: String?,
    val dateRetour: String?,
    val photo: String?,
    val nb_voyageur: Int?,
    var list_activity: List<Activity>?,
    var list_flights: List<Flight>?,
    var list_hotels : List<Hotel>?,
    val destination : String?,
    val budget : String?

) : Serializable {


//    companion object { /*  all: même chose que :ListClient<>*/
//        val all = (1..20).map{
//            Voyage(it,"titre$it", "debut$it","fin$it", R.drawable.destination1,it)
//        }.toMutableList()
//
//    }
}
