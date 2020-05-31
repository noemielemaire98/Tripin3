package com.example.tripin.find.hotel


// @GET ("properties/get-details")
object ModelDetailsHotel {

    data class Welcome (
        val result: String,
        val data: Data,
        val transportation: Transportation,
        val neighborhood: Neighborhood
    )

    data class Data (
        val body: Body,
        val common: Common
    )

    data class Body (
        val pdpHeader: PDPHeader,
        val overview: Overview,
        val hotelWelcomeRewards: HotelWelcomeRewards,
        val propertyDescription: PropertyDescription,
        val guestReviews: GuestReviews,
        val atAGlance: AtAGlance,
        val amenities: List<AmenityClass>,
        val smallPrint: SmallPrint,
        val roomsAndRates: RoomsAndRates,
        val specialFeatures: SpecialFeatures,
        val miscellaneous: Miscellaneous,
        val pageInfo: PageInfo,
        val trustYouReviewsCredit: Boolean
    )

    data class AmenityClass (
        val heading: String,
        val listItems: List<ListItem>
    )

    data class ListItem (
        val heading: String,
        val listItems: List<String>
    )

    data class AtAGlance (
        val keyFacts: KeyFacts,
        val travellingOrInternet: TravellingOrInternet,
        val transportAndOther: TransportAndOther
    )

    data class KeyFacts (
        val hotelSize: List<String>,
        val arrivingLeaving: List<String>,
        val specialCheckInInstructions: List<String>,
        val requiredAtCheckIn: List<String>
    )

    data class TransportAndOther (
        val transport: Transport,
        val otherInformation: List<String>,
        val otherInclusions: List<Any?>
    )

    data class Transport (
        val transfers: List<Any?>,
        val parking: List<String>,
        val offsiteTransfer: List<Any?>
    )

    data class TravellingOrInternet (
        val travelling: Travelling,
        val internet: List<String>
    )

    data class Travelling (
        val children: List<String>,
        val pets: List<String>,
        val extraPeople: List<Any?>
    )

    data class GuestReviews (
        val brands: Brands,
        val trustYouReviews: List<Any?>
    )

    data class Brands (
        val scale: Long,
        val formattedScale: String,
        val rating: Double,
        val formattedRating: String,
        val lowRating: Boolean,
        val total: Long
    )

    data class HotelWelcomeRewards (
        val applies: Boolean,
        val info: String
    )

    data class Miscellaneous (
        val pimmsAttributes: String,
        val showLegalInfoForStrikethroughPrices: Boolean
    )

    data class Overview (
        val overviewSections: List<OverviewSection>
    )

    data class OverviewSection (
        val title: String? = null,
        val type: String,
        val content: List<String>,
        val contentType: String
    )

    data class PageInfo (
        val pageType: String,
        val errors: List<Any?>,
        val errorKeys: List<Any?>
    )

    data class PDPHeader (
        val hotelID: String,
        val destinationID: String,
        val pointOfSaleID: String,
        val currencyCode: String,
        val occupancyKey: String,
        val hotelLocation: HotelLocation
    )



    data class HotelLocation (
        val coordinates: Coordinates,
        val resolvedLocation: String,
        val locationName: String
    )

    data class Coordinates (
        val latitude: Double,
        val longitude: Double
    )

    data class PropertyDescription (
        val clientToken: String,
        val localisedAddress: Address,
        val address: Address,
        val priceMatchEnabled: Boolean,
        val name: String,
        val starRatingTitle: String,
        val starRating: Long,
        val featuredPrice: FeaturedPrice,
        val mapWidget: MapWidget,
        val roomTypeNames: List<String>,
        val tagline: List<String>,
        val freebies: List<Any?>
    )

    data class Address (
        val countryName: String,
        val cityName: String,
        val postalCode: String,
        val provinceName: String,
        val addressLine1: String,
        val countryCode: String,
        val pattern: String,
        val fullAddress: String
    )

    data class FeaturedPrice (
        val currentPrice: CurrentPrice,
        val priceInfo: String,
        val priceSummary: String,
        val taxInclusiveFormatting: Boolean,
        val bookNowButton: Boolean
    )

    data class CurrentPrice (
        val formatted: String,
        val plain: Long
    )

    data class MapWidget (
        val staticMapURL: String
    )

    data class RoomsAndRates (
        val bookingURL: String,
        val rooms: List<Room>,
        val ratePlanWithOffersExists: Boolean,
        val priceColumnHeading: String
    )

    data class Room (
        val name: String,
        val images: List<Image>,
        val bedChoices: BedChoices,
        val maxOccupancy: MaxOccupancy,
        val additionalInfo: AdditionalInfo,
        val ratePlans: List<RatePlan>
    )

    data class AdditionalInfo (
        val description: String,
        val details: Details
    )

    data class Details (
        val amenities: List<String>
    )

    data class BedChoices (
        val mainOptions: List<String>,
        val extraBedTypes: List<Any?>
    )

    data class Image (
        val caption: String,
        val thumbnailURL: String,
        val fullSizeURL: String
    )


    data class MaxOccupancy (
        val children: Long,
        val total: Long,
        val messageChildren: String,
        val messageTotal: String
    )



    data class RatePlan (
        val occupancy: Occupancy,
        val cancellation: Cancellation,
        val cancellations: List<Cancellation>,
        val features: List<Any?>,
        val welcomeRewards: WelcomeRewards,
        val offers: Offers,
        val price: Price,
        val urgencyMessage: String,
        val roomsLeft: Long,
        val payment: Payment
    )

    data class Cancellation (
        val title: String,
        val free: Boolean,
        val info: String,
        val refundable: Boolean,
        val period: String?
    )


    data class Occupancy (
        val maxAdults: Long,
        val maxChildren: Long
    )

    data class Offers (
        val valueAdds: List<Any?>
    )

    data class Payment (
        val book: Book,
        val noCCRequired: Boolean
    )

    data class Book (
        val caption: String,
        val bookingParamsMixedRatePlan: BookingParamsMixedRatePlan
    )

    data class BookingParamsMixedRatePlan (
        val init: Boolean,
        val bookingAPIVersion: String,
        val numberOfRoomType: String,
        val orderItems: List<OrderItem>,
        val propertyDetailsDisplayRate: String,
        val currency: String,
        val minPrice: String,
        val marketingChannelCode: String,
        val interstitial: String
    )





    data class OrderItem (
        val rateCode: String,
        val roomTypeCode: String,
        val businessModel: String,
        val ratePlanConfiguration: String,
        val arrivalDate: String,
        val departureDate:String,
        val destinationID: String,
        val hotelCityID: String,
        val hotelID: String,
        val sequenceNumber: String,
        val tripID: String,
        val tspid: Long
    )



    data class Price (
        val current: String,
        val unformattedCurrent: Long,
        val info: String,
        val nightlyPriceBreakdown: NightlyPriceBreakdown
    )

    data class NightlyPriceBreakdown (
        val additionalColumns: List<AdditionalColumn>,
        val nightlyPrices: List<Any?>
    )

    data class AdditionalColumn (
        val heading: String,
        val value: String
    )


    data class WelcomeRewards (
        val info: String,
        val collect: Boolean,
        val redeem: Boolean
    )

    data class SmallPrint (
        val alternativeNames: List<String>,
        val mandatoryFees: List<String>,
        val optionalExtras: List<String>,
        val policies: List<String>,
        val mandatoryTaxesOrFees: Boolean,
        val display: Boolean
    )

    data class SpecialFeatures (
        val sections: List<Any?>
    )

    data class Common (
        val pointOfSale: PointOfSale,
        val tracking: Tracking
    )

    data class PointOfSale (
        val numberSeparators: String,
        val brandName: String
    )

    data class Tracking (
        val omniture: Omniture,
        val pageViewBeaconURL: String
    )

    data class Omniture (
        val sProp34: String,
        val sEVar69: String,
        val sCurrencyCode: String,
        val sEVar16: String,
        val sProducts: String,
        val sEVar29: String,
        val sEVar40: String,
        val sEVar95: String,
        val sEVar41: String,
        val sEVar31: String,
        val sEVar4: String,
        val sEVar43: String,
        val sEVar32: String,
        val sEVar34: String,
        val sEVar13: String,
        val sServer: String,
        val sProp28: String,
        val sProp27: String,
        val sProp5: String,
        val sEVar80: String,
        val sProp48: String,
        val sProp36: String,
        val sEVar93: String
    )

    data class Neighborhood (
        val neighborhoodName: String
    )

    data class Transportation (
        val transportLocations: List<TransportLocation>
    )

    data class TransportLocation (
        val category: String,
        val locations: List<Location>
    )

    data class Location (
        val name: String,
        val distance: String,
        val distanceInTime: String
    )

}