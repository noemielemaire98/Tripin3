package com.example.tripin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allactivity")
data class Activity (@PrimaryKey(autoGenerate = true) val id:Int,
                     val title: String,
                     val cover_image_url:String?,
                     val formatted_iso_value : String?,
                     val operational_days : String?,
                     var favoris : Boolean,
                     val about : String?

                        ) {

    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..10).map {

            Activity(it,"Titre$it", "R.drawable.activite1","10€", "tous les jours",false,"about")
        }.toMutableList()

    }
}
