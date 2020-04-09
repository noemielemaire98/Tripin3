package com.example.tripin.model
import com.example.tripin.R
import java.util.*

data class Voyage (val titre: String,
                   val date : String,
                   val photo: Int) {


    companion object { /*  all: même chose que :ListClient<>*/
        val all = (1..20).map{
            Voyage("titre$it", "date$it", R.drawable.destination1)
        }

    }
}