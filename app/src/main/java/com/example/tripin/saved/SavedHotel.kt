package com.example.tripin.saved

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tripin.HotelsAdapter
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import kotlinx.android.synthetic.main.activity_saved_hotel.*
import kotlinx.coroutines.runBlocking

class SavedHotel : AppCompatActivity() {
    private var hotelDao : HotelDao? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val hotels = hotelDao?.getHotels()
            hotels_saved_recyclerview.adapter =
                HotelsAdapter(hotels!!)
        }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_hotel)

        hotels_saved_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "allhotels")
                .build()



        hotelDao = database.getHotelDao()
        runBlocking {
            val hotels = hotelDao?.getHotels()
            hotels_saved_recyclerview.adapter =
                HotelsAdapter(hotels!!)
            }

    }

}


