package com.example.tripin.saved

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.find.flight.FlightsAdapter
import com.example.tripin.model.Flight
import kotlinx.android.synthetic.main.activity_saved_flight.*
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class SavedFlightFragment : Fragment() {

    private var flightDaoSaved: FlightDao? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_saved_flight, container, false)

        val rv = root.findViewById<RecyclerView>(R.id.savedFlights_recyclerview)
        val iv_noFLight = root.findViewById<ImageView>(R.id.noFlightsImage)
        val layout_noflight = root.findViewById<RelativeLayout>(R.id.layoutNoSavedFlight)

       rv.layoutManager = LinearLayoutManager(requireActivity().baseContext)

       iv_noFLight.setOnClickListener {
            val intent = Intent(requireActivity().baseContext, MainActivity::class.java)
            intent.putExtra("switchView", 1)
            startActivity(intent)
        }

        val database =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "savedDatabase")
                .build()

        flightDaoSaved = database.getFlightDao()

        runBlocking {
            if (!flightDaoSaved?.getFlights().isNullOrEmpty()) {
                layout_noflight.visibility = View.GONE
            } else {
                layout_noflight.visibility = View.VISIBLE
            }
        }

        return root
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
                savedFlights_recyclerview.adapter =
                    FlightsAdapter(flightsList, requireView())

            } else {
                layoutNoSavedFlight.visibility = View.VISIBLE
            }
        }
    }

}
