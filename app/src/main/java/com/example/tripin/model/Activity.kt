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
                     val reviews_avg : Double?,
                     val category : List<String>?,
                     val about : String?

                        ) : Parcelable {

    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..10).map {

            val ll = listOf<String>("aa","bb")

            Activity("id$it","Titre$it", "R.drawable.activite1","10€", "tous les jours",5.5,ll,"about")
        }.toMutableList()

    }
}
