package com.example.tripin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.tripin.model.Flight
import kotlinx.android.synthetic.main.activity_detail_flights.*

class DetailFlights : AppCompatActivity() {

    private var flights = emptyArray<Flight>()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_flights)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        @Suppress("UNCHECKED_CAST")
        flights = intent.getSerializableExtra("flights") as Array<Flight>


        priceTotal.text = "${flights[0].prixTotal} €"
        dateDepart.text = flights[0].dateDepart
        priceAdult.text = flights[0].prixParAdult.toString()

        var retourDone = false
        flights.map {
            if(it.retour == 1 && !retourDone) {
                dateDepartRetour.text = it.dateDepart
                retourDone = true
            }
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
