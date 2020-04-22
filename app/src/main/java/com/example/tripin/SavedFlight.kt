package com.example.tripin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.model.Flight
import kotlinx.android.synthetic.main.activity_saved_flight.*
import kotlinx.coroutines.runBlocking

class SavedFlight : AppCompatActivity() {

    private var flightDaoSaved: FlightDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_flight)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val savedFlightsRecyclerView =
            findViewById<View>(R.id.savedFlights_recyclerview) as RecyclerView
        savedFlightsRecyclerView.layoutManager = LinearLayoutManager(this)

        noFlightsImage.setOnClickListener {
            val intent = Intent(this, FindFlight::class.java)
            finish()
            startActivity(intent)
        }

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()

        flightDaoSaved = database.getFlightDao()

        runBlocking {
            if (flightDaoSaved?.getFlights() != null) {
                layoutNoSavedFlight.visibility = View.GONE
            } else {
                layoutNoSavedFlight.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val flights = flightDaoSaved?.getFlights()
            if (!flights.isNullOrEmpty()) {
                layoutNoSavedFlight.visibility = View.GONE
                val flightsList: MutableList<MutableList<Flight>> = mutableListOf()
                var testlist: MutableList<Flight> = mutableListOf()
                var uuid = flights[0].uuid
                var position = 0
                flights.map {
                    val flight = flights[position]
                    if (it.uuid != uuid && position != 0) {
                        flightsList.add(testlist)
                        testlist = mutableListOf()
                        uuid = it.uuid
                    }
                    testlist.add(flight)
                    position += 1
                    if (flights.size == position + 1) {
                        flightsList.add(testlist)
                    }


                }
                savedFlights_recyclerview.adapter = FlightsAdapter(flightsList)

            } else {
                layoutNoSavedFlight.visibility = View.VISIBLE
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
