package com.example.tripin.saved

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tripin.find.hotel.HotelsAdapter
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.find.hotel.FindHotelActivity
import com.example.tripin.find.hotel.FindHotelFragment
import kotlinx.android.synthetic.main.activity_saved_hotel.*
import kotlinx.coroutines.runBlocking

class SavedHotel : AppCompatActivity() {
    private var hotelDaoSaved : HotelDao? = null
    private var list_favoris = arrayListOf<Boolean>()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_hotel)

        hotels_saved_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        noFlightsImage.setOnClickListener {
            val intent = Intent(this, FindHotelFragment::class.java)
            finish()
            startActivity(intent)
        }


        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabaseHotels")
                .build()



        hotelDaoSaved = databasesaved.getHotelDao()
        runBlocking {
            val hotels = hotelDaoSaved?.getHotels()
            hotels?.map {
                list_favoris.add(true)
            }

            if(!hotels.isNullOrEmpty()){
                layoutNoSavedHotel.visibility = View.GONE
                hotels_saved_recyclerview.adapter =
                    HotelsAdapter(hotels!!, list_favoris)

            }else{
                layoutNoSavedHotel.visibility = View.VISIBLE
                layoutRecyclerView_HotelsSaved.visibility = View.GONE
            }

        }

    }


    override fun onResume() {
        super.onResume()

       runBlocking {
            val hotels = hotelDaoSaved?.getHotels()

           hotels?.map {
               list_favoris.add(true)
           }

            if(!hotels.isNullOrEmpty()){
            hotels_saved_recyclerview.adapter =
                HotelsAdapter(hotels!!, list_favoris)
                layoutNoSavedHotel.visibility = View.GONE}
            else {
                layoutRecyclerView_HotelsSaved.visibility = View.GONE
                layoutNoSavedHotel.visibility = View.VISIBLE
            }
        }

        }




}


