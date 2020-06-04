package com.example.tripin.find.hotel

import com.google.gson.JsonObject
import com.squareup.moshi.Json

object ModelRapid {


    //    @GET("locations/search")
    data class City (
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
        val destinationId: String,
        val landmarkCityDestinationID: String? = null,
        val type: String,
        val caption: String,
        val redirectPage: String,
        val latitude: Double,
        val longitude: Double,
        val name: String
    )









    // @GET ("properties/list")
    data class Hotels (
        val result: String,
        val data: Data
    )
    data class Data (
        val body: Body,
        val common: Common
    )

    data class Body (
        val header: String,
        val query: Query,
        val searchResults: SearchResults,
        val sortResults: SortResults,
        val filters: Filters,
        val pointOfSale: BodyPointOfSale,
        val miscellaneous: Miscellaneous,
        val pageInfo: PageInfo
    )

    data class Filters (
        val applied: Boolean,
        val name: Name,
        val starRating: StarRating,
        val guestRating: GuestRating,
        val landmarks: Landmarks,
        val neighbourhood: Neighbourhood,
        val accommodationType: Accessibility,
        val facilities: Accessibility,
        val accessibility: Accessibility,
        val themesAndTypes: Accessibility,
        val price: FiltersPrice,
        val paymentPreference: PaymentPreference,
        val welcomeRewards: WelcomeRewards
    )

    data class Accessibility (
        val applied: Boolean,
        val items: List<AccessibilityItem>
    )

    data class AccessibilityItem (
        val label: String,
        val value: String
    )

    data class GuestRating (
        val range: GuestRatingRange
    )

    data class GuestRatingRange (
        val min: Max,
        val max: Max
    )

    data class Max (
        val defaultValue: Long
    )

    data class Landmarks (
        val selectedOrder: List<Any?>,
        val items: List<AccessibilityItem>,
        val distance: List<Any?>
    )

    data class Name (
        val item: NameItem,
        val autosuggest: Autosuggest
    )

    data class Autosuggest (
        val additionalURLParams: AdditionalURLParams
    )

    data class AdditionalURLParams (
        val resolvedLocation: String,
        val qDestination: String,
        val destinationID: String
    )

    data class NameItem (
        val value: String
    )

    data class Neighbourhood (
        val applied: Boolean,
        val items: List<NeighbourhoodItem>
    )

    data class NeighbourhoodItem (
        val label: String,
        val value: Long
    )

    data class PaymentPreference (
        val items: List<AccessibilityItem>
    )

    data class FiltersPrice (
        val label: String,
        val range: PriceRange,
        val multiplier: Long
    )

    data class PriceRange (
        val min: Max,
        val max: Max,
        val increments: Long
    )

    data class StarRating (
        val applied: Boolean,
        val items: List<StarRatingItem>
    )

    data class StarRatingItem (
        val value: Long
    )

    data class WelcomeRewards (
        val label: String,
        val items: List<AccessibilityItem>
    )

    data class Miscellaneous (
        val pageViewBeaconURL: String
    )

    data class PageInfo (
        val pageType: String
    )

    data class BodyPointOfSale (
        val currency: Currency
    )

    data class Currency (
        val code: String,
        val symbol: String,
        val separators: String,
        val format: String
    )

    data class Query (
        val destination: Destination
    )

    data class Destination (
        val id: String,
        val value: String,
        val resolvedLocation: String
    )

    data class SearchResults (
        val totalCount: Long,
        val results: List<Result>,
        val pagination: Pagination
    )

    data class Pagination (
        val currentPage: Long,
        val pageGroup: String,
        val nextPageNumber: Long,
        val nextPageGroup: String
    )

    data class Result (
        val id: Long, //TODO : hotelID
        val name: String, //TODO : hotelName
        val thumbnailUrl: String, //TODO : image_url
        val starRating: Float, //TODO : rate
        //val urls: Badging,
        val address: Address, //TODO : adresse
        val guestReviews: GuestReviews,
        val landmarks: List<Landmark>,
        val ratePlan: RatePlan,
        val neighbourhood: String,
        //val deals: Badging,
        //val messaging: Badging,
        //val badging: Badging,
        val coordinate: Coordinate, //TODO : adresse (2)
        val providerType: String,
        val supplierHotelID: Long,
        val isAlternative: Boolean
    )

    data class Address (
        val streetAddress: String,
        val extendedAddress: String,
        val locality: String,
        val postalCode: String,
        val region: String,
        val countryName: String,
        val countryCode: String
    )

    data class Coordinate (
        val lat: Double, //TODO : latitude
        val lon: Double //TODO : longitude
    )

    data class GuestReviews (
        val unformattedRating: Double,
        val rating: String,
        val total: Long,
        val scale: Long,
        val badge: String,
        val badgeText: String
    )

    data class Landmark (
        val label: String,
        val distance: String
    )

    data class RatePlan (
        val price: RatePlanPrice,
        val features: Features
    )

    data class Features (
        val paymentPreference: Boolean,
        val noCCRequired: Boolean
    )

    data class RatePlanPrice (
        val current: String, //TODO : prix
        val exactCurrent: Double
    )

    data class SortResults (
        val options: List<Option>,
        val distanceOptionLandmarkID: Long
    )

    data class Option (
        val label: String,
        val itemMeta: String,
        val choices: List<OptionChoice>,
        val enhancedChoices: List<EnhancedChoice>,
        val selectedChoiceLabel: String? = null
    )

    data class OptionChoice (
        val label: String,
        val value: String,
        val selected: Boolean
    )

    data class EnhancedChoice (
        val label: String,
        val itemMeta: String,
        val choices: List<EnhancedChoiceChoice>
    )

    data class EnhancedChoiceChoice (
        val label: String,
        val id: Long
    )

    data class Common (
        val pointOfSale: CommonPointOfSale,
        val tracking: Tracking
    )

    data class CommonPointOfSale (
        val numberSeparators: String,
        val brandName: String
    )

    data class Tracking (
        val omniture: Omniture
    )

    data class Omniture (
        val sProp33: String,

        @Json(name = "s.prop32")
        val sProp32: String,

        @Json(name = "s.prop74")
        val sProp74: String,

        @Json(name = "s.products")
        val sProducts: String,

        @Json(name = "s.eVar16")
        val sEVar16: String,

        @Json(name = "s.eVar40")
        val sEVar40: String,

        @Json(name = "s.eVar41")
        val sEVar41: String,

        @Json(name = "s.eVar63")
        val sEVar63: String,

        @Json(name = "s.eVar42")
        val sEVar42: String,

        @Json(name = "s.eVar4")
        val sEVar4: String,

        @Json(name = "s.eVar43")
        val sEVar43: String,

        @Json(name = "s.eVar2")
        val sEVar2: String,

        @Json(name = "s.eVar24")
        val sEVar24: String,

        @Json(name = "s.eVar7")
        val sEVar7: String,

        @Json(name = "s.server")
        val sServer: String,

        @Json(name = "s.eVar6")
        val sEVar6: String,

        @Json(name = "s.prop29")
        val sProp29: String,

        @Json(name = "s.prop27")
        val sProp27: String,

        @Json(name = "s.eVar9")
        val sEVar9: String,

        @Json(name = "s.eVar69")
        val sEVar69: String,

        @Json(name = "s.currencyCode")
        val sCurrencyCode: String,

        @Json(name = "s.eVar29")
        val sEVar29: String,

        @Json(name = "s.prop9")
        val sProp9: String,

        @Json(name = "s.prop8")
        val sProp8: String,

        @Json(name = "s.eVar95")
        val sEVar95: String,

        @Json(name = "s.prop7")
        val sProp7: String,

        @Json(name = "s.eVar31")
        val sEVar31: String,

        @Json(name = "s.eVar32")
        val sEVar32: String,

        @Json(name = "s.eVar33")
        val sEVar33: String,

        @Json(name = "s.eVar34")
        val sEVar34: String,

        @Json(name = "s.eVar13")
        val sEVar13: String,

        @Json(name = "s.prop18")
        val sProp18: String,

        @Json(name = "s.prop5")
        val sProp5: String,

        @Json(name = "s.prop15")
        val sProp15: String,

        @Json(name = "s.prop3")
        val sProp3: String,

        @Json(name = "s.prop14")
        val sProp14: String,

        @Json(name = "s.prop36")
        val sProp36: String,

        @Json(name = "s.eVar93")
        val sEVar93: String,

        @Json(name = "s.prop2")
        val sProp2: String
    )





}