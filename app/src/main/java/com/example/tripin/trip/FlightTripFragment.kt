package com.example.tripin.trip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.find.flight.FlightsAdapterTrip
import com.example.tripin.model.Flight
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class FlightTripFragment : Fragment() {

    private var listFav = arrayListOf<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val voyage = (activity as? DetailVoyage2)!!.voyage

        val view = inflater.inflate(R.layout.activity_saved_flight, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.savedFlights_recyclerview)
        val imageNoFlights = view.findViewById<ImageView>(R.id.noFlightsImage)
        val layoutNoFlights = view.findViewById<RelativeLayout>(R.id.layoutNoSavedFlight)

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        imageNoFlights.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("switchView", 1)
            startActivity(intent)
            activity?.finish()
        }

        runBlocking {
            val flights = voyage!!.list_flights
            flights?.map {
                listFav.add(true)
            }

            if (!flights.isNullOrEmpty()) { // S'il y a des vols de trouvés
                layoutNoFlights.visibility = View.GONE
                val flightsList: MutableList<MutableList<Flight>> = mutableListOf()
                var testlist: MutableList<Flight> = mutableListOf()
                var uuid = flights[0].uuid
                var position = 0
                // map pour regrouper les vols d'un même itinéraire

                flights.map {
                    val flight = flights[position]
                    if (position != 0 && it.uuid != uuid) { // Si appartient au même itinéraire
                        flightsList.add(testlist)
                        testlist = mutableListOf()
                        uuid = it.uuid
                    }
                    Log.d("flights", position.toString())
                    testlist.add(flight)
                    position += 1
                    if (flights.size == position) {
                        flightsList.add(testlist)
                        Log.d("flights", flightsList.toString())
                    }
                }
                recyclerView.adapter = FlightsAdapterTrip(flightsList, voyage)
            } else {
                layoutNoFlights.visibility = View.VISIBLE
            }
        }

        return view
    }

}
