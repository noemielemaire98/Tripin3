package com.example.tripin.find.hotel

object ModelRapid {

    data class Traffic (
        val term: String,
        val moresuggestions: Long,
        val autoSuggestInstance: Any? = null,
        val trackingID: String,
        val misspellingfallback: Boolean,
        val suggestions: List<Suggestion>
    )

    data class Suggestion (
        val group: String,
        val entities: List<Entity>
    )

    data class Entity (
        val geoID: String,
        val destinationID: String,
        val landmarkCityDestinationID: String? = null,
        val type: String,
        val caption: String,
        val redirectPage: String,
        val latitude: Double,
        val longitude: Double,
        val name: String
    )

}