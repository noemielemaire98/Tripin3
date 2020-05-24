package com.example.tripin.find.hotel

object ModelRapid {

    data class Rapid(
        val type : String,
        val properties : Property
    )

    data class Property(
        val term : String,
        val moresuggestions : Int
    )

}