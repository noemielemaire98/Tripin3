package com.example.tripin.find.activity

object ModelMusement {

    data class Welcome (
        val meta: Meta,
        val data: List<Datum>
    )

    data class Datum (
        val operational_days: String,
        val maxConfirmationTime: String,
        val cutoffTime: String,
        val bookingType: String,
        val uuid: String,
        val city: City,
        val saves: Long,
        val title: String,
        val relevance: Long,
        val relevanceVenue: Long,
        val must_see: Boolean,
        val lastChance: Boolean,
        val top_seller: Boolean,
        val voucherAccessUsage: String,
        val temporary: Boolean,
        val description: String,
        val about: String,
        val meetingPoint: String,
        val durationRange: DurationRange,
        val validity: String,
        val hasPriceInfoOnDate: Boolean,
        val open: Boolean,
        val ticketNotIncluded: Boolean,
        val likelyToSellOut: Boolean,
        val specialOffer: Boolean,
        val exclusive: Boolean,
        val bestPrice: Boolean,
        val daily: Boolean,
        val languages: List<Feature>,
        val groupSize: List<Feature>,
        val food: List<Any?>,
        val services: List<Feature>,
        val features: List<Feature>,
        val isAvailableToday: Boolean,
        val isAvailableTomorrow: Boolean,
        val cover_image_url: String,
        val serviceFee: OriginalRetailPrice,
        val retail_price: OriginalRetailPrice,
        val retailPriceWithoutServiceFee: OriginalRetailPrice,
        val originalRetailPriceWithoutServiceFee: OriginalRetailPrice,
        val originalRetailPrice: OriginalRetailPrice,
        val discount: Long,
        val categories: List<Category>,
        val reviewsNumber: Long,
        val reviews_avg: Double,
        val reviewsAggregatedInfo: Map<String, Long>,
        val latitude: Double,
        val longitude: Double,
        val url: String,
        val flavours: List<Flavour>,
        val verticals: List<Vertical>,
        val supplier: Supplier,
        val giftable: Boolean,
        val buyMultiplier: Long
    )

    data class Category (
        val id: Long,
        val name: String,
        val level: String,
        val code: String,
        val eventImageURL: String,
        val coverImageURL: String,
        val url: String
    )

    data class City (
        val id: Long,
        val name: String,
        val country: Country,
        val coverImageURL: String,
        val url: String,
        val timeZone: String
    )

    data class Country (
        val id: Long,
        val name: String,
        val isoCode: String
    )

    data class DurationRange (
        val max: String
    )

    data class Feature (
        val code: String,
        val name: String
    )

    data class Flavour (
        val id: Long,
        val name: String,
        val active: Boolean,
        val slug: String
    )

    data class OriginalRetailPrice (
        val currency: Currency,
        //val value: Long,
        val formattedValue: String,
        val formatted_iso_value: String
    )

    enum class Currency {
        EUR
    }

    data class Supplier (
        val uuid: String,
        val logoURL: String
    )

    data class Vertical (
        val id: Long,
        val name: String,
        val active: Boolean,
        val code: String,
        val slug: String,
        val url: String,
        val metaTitle: String,
        val metaDescription: String,
        val coverImageURL: String,
        val relevance: Long
    )

    data class Meta (
        val count: Long,
        val matchType: String,
        val matchNames: List<String>,
        val matchIDS: List<String>
    )
}