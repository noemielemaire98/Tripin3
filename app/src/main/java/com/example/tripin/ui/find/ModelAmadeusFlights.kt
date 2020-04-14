package com.example.tripin.ui.find

import com.beust.klaxon.*

object ModelAmadeusFlights {

    private val klaxon = Klaxon()

    data class Result(
        val meta: Meta,
        val data: List<Datum>,
        val dictionaries: Dictionaries
    ) {
        public fun toJson() = klaxon.toJsonString(this)

        companion object {
            public fun fromJson(json: String) = klaxon.parse<Result>(json)
        }
    }

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
        val aircraft: SegmentAircraft,
        val operating: Operating,
        val duration: String,
        val id: String,
        val numberOfStops: Long,
        val blacklistedInEU: Boolean
    )

    data class SegmentAircraft(
        val code: String
    )

    data class Arrival(
        val iataCode: String,
        val at: String,
        val terminal: String? = null
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
        @Json(name = "travelerId")
        val travelerID: String,

        val fareOption: String,
        val travelerType: String,
        val price: TravelerPricingPrice,
        val fareDetailsBySegment: List<FareDetailsBySegment>
    )

    data class FareDetailsBySegment(
        @Json(name = "segmentId")
        val segmentID: String,

        val cabin: String,
        val fareBasis: String,

        @Json(name = "class")
        val fareDetailsBySegmentClass: String,

        val includedCheckedBags: IncludedCheckedBags
    )

    data class IncludedCheckedBags(
        val weight: Long,
        val weightUnit: String
    )

    data class TravelerPricingPrice(
        val currency: String,
        val total: String,
        val base: String
    )

    data class Dictionaries(
        val locations: Map<String, Location>,
        val aircraft: DictionariesAircraft,
        val currencies: Currencies,
        val carriers: Carriers
    )

    data class DictionariesAircraft(
        @Json(name = "747")
        val the747: String
    )

    data class Carriers(
        @Json(name = "TG")
        val tg: String
    )

    data class Currencies(
        @Json(name = "EUR")
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