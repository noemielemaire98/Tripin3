package com.example.tripin.ui.find

import com.google.gson.annotations.SerializedName

object ModelAmadeusHotels {
    data class Result(
        val type: String,
        val hotel: Hotel,
        val available: Boolean,
        val offers: ArrayList<Offer>,
        val self: String)

data class Hotel(
    val type: String,
    val hotelId: String,
    val chainCode: String,
    val dupeId: Int,
    val name: String,
    val rating: Int,
    val cityCode: String,
    val latitude: Double,
    val longitude: Double,
    val hotelDistance: HotelDistance,
    val address: Address,
    val contact: Contact,
    val amenities: ArrayList<String>,
    val media: ArrayList<Media>

    )

data class HotelDistance(
    val distance: Double,
    val distanceUnit: String)

data class Address(
    val lines: ArrayList<String>, //"lines": [ "LEFISTON STREET" ],
    val cityName: String,
    val countryCode: String)

data class Contact(
    val phone: String,
    val fax: String)

data class Media(
    val uri: String,
    val category: String)

data class Offer(
    val id: String,
    val rateCode: String,
    val room: Room,
    val guest: Guest,
    val price: Price)

data class Room(
   val type: String,
   val typeEstimated: TypeEstimated,
   val description: Description)

data class TypeEstimated(
    val category: String,
    val beds: Int,
    val bedType : String)

data class Description(
    val lang: String,
    val text: String)

data class Guest(
    val adults: Int)

data class Price(
    val currency: String,
    val base: Double,
    val total: Double,
    val variations: Variation)

data class Variation(
    val average: Average,
    val changes: ArrayList<Change>)

data class Average(
    val base: Double)

data class Change(
    val startDate: String,
    val endDate: String,
    val base: Double)

}