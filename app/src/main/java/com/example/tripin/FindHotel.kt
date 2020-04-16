package com.example.tripin

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val hotelsRecyclerView = findViewById<View>(R.id.hotels_recyclerview) as RecyclerView
        hotelsRecyclerView .layoutManager=  LinearLayoutManager(this)

        btn_search.setOnClickListener {
            beginSearch() }

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "gestionhotels").build()

        hotelDao=database.getHotelDao()



    }


    private fun beginSearch(){
        runBlocking {
            //vider la liste
        }

        //création d'un client Amadeus
        val amadeus = Amadeus
            .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
            .build()


        GlobalScope.launch{
            val hotelOffersSearches= amadeus.shopping.hotelOffers[Params
                .with("cityCode", "MAD")]

          //  Log.d("Offres", hotelOffersSearches.contentToString())
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
