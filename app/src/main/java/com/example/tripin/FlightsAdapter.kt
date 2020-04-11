package com.example.tripin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.model.Flight
import com.example.tripin.ui.find.ModelSkyScannerFlights
import com.example.tripin.ui.find.toDirect
import kotlinx.android.synthetic.main.flights_view.view.*
import kotlinx.android.synthetic.main.voyage_view.view.*

class FlightsAdapter(val flights : List<Flight>) : RecyclerView.Adapter<FlightsAdapter.FlightsViewHolder>() {
    class FlightsViewHolder(val flightsView : View) : RecyclerView.ViewHolder(flightsView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightsViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.flights_view, parent, false)
        return FlightsViewHolder(view)
    }

    override fun getItemCount(): Int = flights.size


    override fun onBindViewHolder(holder: FlightsViewHolder, position: Int) {

        flights.map {
            val Flight = flights[position]
            holder.flightsView.flights_title_textview.text = "${Flight.MinPrice} €"
            holder.flightsView.flights_date_textview.text = "${Flight.DepartureDate}"
            holder.flightsView.flights_direct_textview.text = "${Flight.Direct.toDirect()}"
            holder.flightsView.flights_imageview.setImageResource(R.drawable.ic_flight_black_24dp)
        }
    }

}