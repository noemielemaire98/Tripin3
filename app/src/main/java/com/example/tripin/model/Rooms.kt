package com.example.tripin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Rooms (
    var idHotel : Int,
    var nameRoom : String,
    var imageIndiceRoom : List<String>,
    var imagesRoom : List<String>,
    var descriptionRoom : String,
    var occupancyRoom : String,
    var amenitiesRoom : List<String>,
    var priceNight : String,
    var price : String,
    var promo : String?,
    var checkIn: String,
    var checkOut : String,
    var listOccupants: MutableList<String>?
) : Parcelable
