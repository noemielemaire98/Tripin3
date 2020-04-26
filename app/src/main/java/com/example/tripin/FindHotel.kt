package com.example.tripin

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.model.Hotel
import kotlinx.android.synthetic.main.activity_find_hotel.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class FindHotel : AppCompatActivity() {


    private var hotelDao: HotelDao? = null
    val list_hotels : MutableList<Hotel> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_hotel)
        hotels_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "allhotels").build()
        hotelDao=database.getHotelDao()


        // TODO : faire apparaitre le clavier


       btn_search.setOnClickListener {

            //création d'un client Amadeus
            val amadeus = Amadeus
                .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
                .build()

            runBlocking{

                list_hotels.clear()
                val bdd_hotels = hotelDao?.getHotels()

                GlobalScope.launch{
                    val hotelOffersSearches = amadeus.shopping.hotelOffers[Params.with("cityCode", "LON")]

                        runOnUiThread(java.lang.Runnable {
                            //permet d'appeler le block sur chacun des éléments d'une collection (== boucle for)
                            hotelOffersSearches.map { itHotelOffer ->
                                var favoris = false
                                var idHotel = itHotelOffer.hotel.hotelId
                                bdd_hotels?.forEach {
                                    if (it.hotelId == idHotel) {
                                        favoris = true
                                    }
                                }

                                var description: String
                                var email : String
                                var tel : String
                                var uri: String
                                if (itHotelOffer.hotel.description == null){
                                    description = "Description non disponible"
                                }else{
                                    description = itHotelOffer.hotel.description.text
                                }
                                if (itHotelOffer.hotel.contact == null){
                                    email = "Email non disponible"
                                    tel = "Téléphone non disponible"
                                }else{
                                    //TODO : Resoudre problème récupération email
                                    email = "email"
                                    tel = itHotelOffer.hotel.contact.phone
                                }
                                if (itHotelOffer.hotel.media == null){
                                    uri = "0"
                                }else{
                                    uri = itHotelOffer.hotel.media[0].uri }


                                val hotel = Hotel(
                                    0,
                                    itHotelOffer.hotel.hotelId,
                                    itHotelOffer.hotel.name,
                                    description,
                                    itHotelOffer.hotel.rating,
                                    uri,
                                    itHotelOffer.hotel.address.lines.joinToString(),
                                    email,
                                    tel,
                                favoris)
                                runBlocking {
                                    list_hotels.add(hotel)
                                }
                            }


                            hotels_recyclerview.adapter=HotelsAdapter(list_hotels ?: emptyList())
        })
        }
  }

        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
