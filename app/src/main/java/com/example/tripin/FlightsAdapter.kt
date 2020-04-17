package com.example.tripin

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.data.FlightDao
import com.example.tripin.model.Flight
import kotlinx.android.synthetic.main.flights_view.view.*
//import org.junit.rules.Timeout.millis
import java.time.Duration
import java.util.concurrent.TimeUnit


class FlightsAdapter(val flightslist: MutableList<MutableList<Flight>>) :
    RecyclerView.Adapter<FlightsAdapter.FlightsViewHolder>() {
    private var flightDao: FlightDao? = null

    class FlightsViewHolder(val flightsView: View) : RecyclerView.ViewHolder(flightsView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightsViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.flights_view, parent, false)
        return FlightsViewHolder(view)
    }

    override fun getItemCount(): Int = flightslist.size


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FlightsViewHolder, position: Int) {

        flightslist.map {
            var flights = flightslist[position]
            var pot = 0
            //        var returnTravel = false
            it.map {
                val Flight = flights[pot]
                if (flights[pot].retour == 0 && (pot == 0
                            || (flights[pot - 1].travelId == flights[pot].travelId
                            && flights[pot - 1].dureeVol != flights[pot].dureeVol))
                ) {

                    holder.flightsView.flights_price_textview.text = "${Flight.prixTotal} €"
                    holder.flightsView.heures_aller.text = Flight.dateDepart

                    holder.flightsView.duree.text = Flight.dureeVol
                    holder.flightsView.flights_direct_textview.text =
                        if (Flight.nbEscales != 0) "${Flight.nbEscales} escale(s)" else "Direct"
                    holder.flightsView.flights_imageView.setImageResource(R.drawable.ic_flight_black_24dp)

                } else if (flights[pot].retour == 1 && flights[pot - 1].dureeVol != flights[pot].dureeVol) {

                    //         val Flight = flights[pot]
                    holder.flightsView.heures_retour.text = Flight.dateDepart

                    holder.flightsView.duree_retour.text = Flight.dureeVol
                    holder.flightsView.flights_direct_return_textview.text =
                        if (Flight.nbEscales != 0) "${Flight.nbEscales} escale(s)" else "Direct"
                    holder.flightsView.flights_imageView_return.setImageResource(R.drawable.ic_flight_black_24dp)

                }
                if (flights[pot].retour == 0 && (pot == flights.size - 1 || flights[pot + 1].dureeVol != flights[pot].dureeVol)) {
                    holder.flightsView.flights_date_textview.text =
                        Flight.dateDepart
                    if (pot == flights.size - 1) holder.flightsView.layout_retour.visibility =
                        View.GONE
                }
                if (flights[pot].retour == 1 && pot == flights.size - 1) {
                    holder.flightsView.flights_date_return_textview.text =
                        Flight.dateDepart
                }
                pot += 1

            }
        }

    }
}