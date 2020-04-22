package com.example.tripin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "allActivities")
data class Activity (@PrimaryKey val uuid:String,
                     val title: String,
                     val cover_image_url:String?,
                     val formatted_iso_value : String?,
                     val operational_days : String?,
                     var favoris : Boolean,
                     val about : String?

                        ) : Parcelable {

    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..10).map {

            Activity("id$it","Titre$it", "R.drawable.activite1","10€", "tous les jours",false,"about")
        }.toMutableList()

    }
}
