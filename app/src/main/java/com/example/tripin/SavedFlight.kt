package com.example.tripin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

class SavedFlight : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_hotel)

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
                layoutNoFlightAvailable.visibility = View.GONE
            } else {
                layoutNoFlightAvailable.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val flights = flightDaoSaved?.getFlights()
            if (!flights.isNullOrEmpty()) {
                layoutNoFlightAvailable.visibility = View.GONE
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
                layoutNoFlightAvailable.visibility = View.VISIBLE
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
