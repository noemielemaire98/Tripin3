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

    private val apiKey = "y1hhd5lSOG9Yl28ThuIYBHeyTgq3eLSJ"
    private val secretKey= "rA1zEyvaTD2H7MV4"
    private var hotelDao: HotelDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_hotel)

      hotels_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "gestionhotels").build()

                 hotelDao=database.getHotelDao()


       btn_search.setOnClickListener {

            //création d'un client Amadeus
            val amadeus = Amadeus
                .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
                .build()

            runBlocking{
                //Faire la fonction dao
               // hotelDao?.deleteHotel()
                GlobalScope.launch{
                    val hotelOffersSearches = amadeus.shopping.hotelOffers[Params.with("cityCode", "MAD")]


                    //le map permet d'appeler la fonction sur chacun des éléments d'une collection (== boucle for)
                        runOnUiThread(java.lang.Runnable {

                            hotelOffersSearches.map { itHotelOffer ->
                                var description: String
                                if (itHotelOffer.hotel.description == null){
                                    description = "Pas de description"
                                }else{
                                    description = itHotelOffer.hotel.description.text
                                }

                                val hotel = Hotel(
                                    0,
                                    itHotelOffer.hotel.hotelId,
                                    itHotelOffer.hotel.name,
                                    description,
                                    itHotelOffer.hotel.rating)

                                runBlocking {
                                    hotelDao?.addHotel(hotel)
                                    Log.d("Hotel ajouté", hotel.toString())
                                }
                            }


                            onResume()
        })
        }
  }

        }










    }

    override fun onResume() {
        super.onResume()

        runBlocking{
            val hotels = hotelDao?.getHotels()
            hotels_recyclerview.adapter=HotelsAdapter(hotels ?: emptyList())
        }
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
