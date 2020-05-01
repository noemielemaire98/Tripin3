package com.example.tripin.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tripin.R
import java.sql.Date
import java.util.*

@Entity(tableName = "myVoyages")
data class Voyage (@PrimaryKey(autoGenerate = true) val id:Int,
                   val titre: String,
                   val date :String,
                   val photo: Int,
                   val nb_voyageur :Int) {


    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..20).map{
            Voyage(it,"titre$it", "debut$it", R.drawable.destination1,it)
        }.toMutableList()

    }
}