package com.example.tripin.find.activity

import com.example.tripin.find.activity.ModelMusement
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivitybyCity {
    @GET("api/v3/activities")
    suspend fun listActivity(
        //@Query("text") city: String,
        @Query("text") city: String,
        @Header("accept-language") lang: String,
        @Header("x-musement-currency") monnaie: String
    ): ModelMusement.Welcome

    @GET("api/v3/cities")
    suspend fun listcities(
        @Header("accept-language") lang: String
    ): List<City>

    @GET("api/v3/cities/{city_id}/activities?sort_by=relevance&limit=20")
    suspend fun listActivitybyCity(
        @Path("city_id") city_id: Int,
        @Header("accept-language") lang: String,
        @Header("x-musement-currency") monnaie: String
    ): ModelMusement.Welcome
}
    data class City(val id : Int = 0,val name : String="",val show_in_popular : Boolean=false)





