package com.example.tripin.find.hotel

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HotelAPI {

    @GET("locations/search")
    suspend fun getLocation (
        @Query("locale") lang : String,
        @Query("query") city : String,
        @Header("x-rapidapi-key") key : String
    ):ModelRapid.City

    @GET ("properties/list")
    suspend fun getHotelsList(
        @Query("destinationId") destinationId :Int,
        @Query ("pageNumber") pageNumber : Int,
        @Query ("checkIn") checkInDate : String,
        @Query ("checkOut") checkOutDate : String,
        @Query("pageSize") pageSize : Int,
        @Query ("adults1") adults1 : Int,
        @Query ("adults2") adults2 : Int?,
        @Query ("adults3") adults3 : Int?,
        @Query ("adults4") adults4 : Int?,
        @Query ("sortOrder") tri : String,
        @Query ("priceMin") priceMin : Int,
        @Query ("priceMax") priceMax : Int,
        @Query("locale") lang : String,
        @Query ("currency") currency : String,
        @Header("x-rapidapi-key") key : String
    ) : ModelRapid.Hotels


    @GET ("properties/get-details")
    suspend fun getHotelDetail(
        @Query ("id") hotelId : Int,
        @Query ("checkIn") checkInDate : String,
        @Query ("checkOut") checkOutDate : String,
        @Query("locale") lang : String,
        @Query ("currency") currency : String,
        @Header("x-rapidapi-key") key : String
    ):ModelDetailsHotel.Welcome

}