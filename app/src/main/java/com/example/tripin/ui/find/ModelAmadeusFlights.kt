package com.example.tripin.ui.find

import com.google.gson.annotations.SerializedName

object ModelAmadeusFlights {
    data class Result(
        val meta: Meta,
        val data: List<Datum>,
        val dictionaries: Dictionaries
    )

    data class Datum(
        val type: String,
        val id: String,
        val source: String,
        val instantTicketingRequired: Boolean,
        val nonHomogeneous: Boolean,
        val oneWay: Boolean,
        val lastTicketingDate: String,
        val numberOfBookableSeats: Long,
        val itineraries: List<Itinerary>,
        val price: DatumPrice,
        val pricingOptions: PricingOptions,
        val validatingAirlineCodes: List<String>,
        val travelerPricings: List<TravelerPricing>
    )

    data class Itinerary(
        val duration: String,
        val segments: List<Segment>
    )

    data class Segment(
        val departure: Arrival,
        val arrival: Arrival,
        val carrierCode: String,
        val number: String,
        val aircraft: Aircraft,
        val operating: Operating,
        val duration: String,
        val id: String,
        val numberOfStops: Long,
        val blacklistedInEU: Boolean
    )

    data class Aircraft(
        val code: String
    )

    data class Arrival(
        val iataCode: String,
        val terminal: String? = null,
        val at: String
    )

    data class Operating(
        val carrierCode: String
    )

    data class DatumPrice(
        val currency: String,
        val total: String,
        val base: String,
        val fees: List<Fee>,
        val grandTotal: String
    )

    data class Fee(
        val amount: String,
        val type: String
    )

    data class PricingOptions(
        val fareType: List<String>,
        val includedCheckedBagsOnly: Boolean
    )

    data class TravelerPricing(
        val travelerID: String,
        val fareOption: String,
        val travelerType: String,
        val price: TravelerPricingPrice,
        val fareDetailsBySegment: List<FareDetailsBySegment>
    )

    data class FareDetailsBySegment(
        val segmentID: String,
        val cabin: String,
        val fareBasis: String,
        @SerializedName("class")
        val fareDetailsBySegmentClass: String,
        val includedCheckedBags: IncludedCheckedBags
    )

    data class IncludedCheckedBags(
        val weight: Long,
        val weightUnit: Float
    )


    data class TravelerPricingPrice(
        val currency: String,
        val total: String,
        val base: String
    )

    data class Dictionaries(
        val locations: Map<String, Location>,
        val aircraft: Map<String, String>,
        val currencies: Currencies,
        val carriers: Carriers
    )

    data class Carriers(
        val pr: String
    )

    data class Currencies(
        val eur: String
    )

    data class Location(
        val cityCode: String,
        val countryCode: String
    )

    data class Meta(
        val count: Long,
        val links: Links
    )

    data class Links(
        val self: String
    )
}