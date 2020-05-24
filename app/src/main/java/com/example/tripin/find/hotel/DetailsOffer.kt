package com.example.tripin.find.hotel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tripin.R

class DetailsOffer : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_hotel) // TODO : change
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}