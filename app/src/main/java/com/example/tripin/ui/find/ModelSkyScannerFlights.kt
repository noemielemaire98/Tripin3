package com.example.tripin.ui.find

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

object ModelSkyScannerFlights {

    data class Result (

        @SerializedName("Quotes") val quotes : List<Quotes> = emptyList(),
        @SerializedName("Places") val places : List<Places> = emptyList(),
        @SerializedName("Carriers") val carriers : List<Carriers> = emptyList(),
        @SerializedName("Currencies") val currencies : List<Currencies> = emptyList()
    )

    data class Quotes(
        @SerializedName("QuoteId")
        val quoteId: Int,
        @SerializedName("MinPrice")
        val minPrice: Double,
        @SerializedName("Direct")
        val direct: Boolean,
        @SerializedName("OutboundLeg")
        val outboundLeg: OutboundLeg,
        @SerializedName("QuoteDateTime")
        val quoteDateTime: String
    )

    data class OutboundLeg(
        @SerializedName("CarrierIds")
        val carrierIds: List<Int>,
        @SerializedName("OriginId")
        val originId: Int,
        @SerializedName("DestinationId")
        val destinationId: Int,
        @SerializedName("DepartureDate")
        val departureDate: String
    )

    data class Places (

        @SerializedName("PlaceId") val placeId : Int,
        @SerializedName("IataCode") val iataCode : String,
        @SerializedName("Name") val name : String,
        @SerializedName("Type") val type : String,
        @SerializedName("SkyscannerCode") val skyscannerCode : String,
        @SerializedName("CityName") val cityName : String,
        @SerializedName("CityId") val cityId : String,
        @SerializedName("CountryName") val countryName : String
    )

    data class Currencies (

        @SerializedName("Code") val code : String,
        @SerializedName("Symbol") val symbol : String,
        @SerializedName("ThousandsSeparator") val thousandsSeparator : String,
        @SerializedName("DecimalSeparator") val decimalSeparator : String,
        @SerializedName("SymbolOnLeft") val symbolOnLeft : Boolean,
        @SerializedName("SpaceBetweenAmountAndSymbol") val spaceBetweenAmountAndSymbol : Boolean,
        @SerializedName("RoundingCoefficient") val roundingCoefficient : Int,
        @SerializedName("DecimalDigits") val decimalDigits : Int
    )

    data class Carriers (

        @SerializedName("CarrierId") val carrierId : Int,
        @SerializedName("Name") val name : String
    )
}