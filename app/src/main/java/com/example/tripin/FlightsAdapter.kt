package com.example.tripin

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.data.FlightDao
import com.example.tripin.model.Flight
import kotlinx.android.synthetic.main.flights_view.view.*
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


//import org.junit.rules.Timeout.millis


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


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: FlightsViewHolder, position: Int) {
        val view = holder.flightsView


        flightslist.map { it ->
            var flights = flightslist[position]
            var pot = 0
            var returnTravel = 0
            it.map {
                if (pot != flights.size) {
                    val Flight = flights[pot]
                    if (flights[pot].retour == 0 && (pot == 0
                                || (flights[pot - 1].travelId == flights[pot].travelId
                                && flights[pot - 1].dureeVol != flights[pot].dureeVol))
                    ) {

                        view.flights_price_textview.text = "${Flight.prixTotal} €"
                        view.aller_date.text = "${Flight.heureDepart} - "
                        view.flights_date_textview.text = Flight.dateDepart

                        view.lieuDepart_aller.text = "${Flight.lieuDepart} - "

                        view.compagnie_aller.text = if(Flight.carrierName != "") Flight.carrierName else Flight.carrierCode

                        view.duree.text = Flight.dureeVol
                        view.flights_direct_textview.text =
                            if (Flight.nbEscales == 1) "${Flight.nbEscales} escale" else if (Flight.nbEscales > 1) "${Flight.nbEscales} escales" else "Direct"

                  //      val mg: AssetManager = view.resources.assets
                        var ims: InputStream? = null
                        try {
                            ims = view.context.assets.open(
                                "optd_airlines_websites_logos/${Flight.carrierCodeLogo.toLowerCase(
                                    Locale.ROOT
                                )}.png")
                            val d = Drawable.createFromStream(ims, null)
                            view.flights_imageView.setImageDrawable(d)
                        } catch (ex: IOException) {
                            //file does not exist
                            view.flights_imageView.setImageResource(R.drawable.ic_flight_black_24dp)
                        } finally {
                            ims?.close()
                        }

                    } else if (flights[pot].retour == 1 && flights[pot - 1].dureeVol != flights[pot].dureeVol) {

                        returnTravel = pot

                        //         val Flight = flights[pot]
                        view.heureDepart_retour.text = "${Flight.heureDepart} - "
                        view.flights_date_return_textview.text = Flight.dateDepart

                        view.lieuDepart_retour.text = "${Flight.lieuDepart} - "

                        view.compagnie_retour.text = if(Flight.carrierName != "") Flight.carrierName else Flight.carrierCode

                        view.duree_retour.text = Flight.dureeVol
                        view.flights_direct_return_textview.text =
                            if (Flight.nbEscales == 1) "${Flight.nbEscales} escale" else if (Flight.nbEscales > 1) "${Flight.nbEscales} escales" else "Direct"

                        var ims: InputStream? = null
                        try {
                            ims = view.context.assets.open(
                                "optd_airlines_websites_logos/${Flight.carrierCodeLogo.toLowerCase(
                                    Locale.ROOT
                                )}.png")
                            val d = Drawable.createFromStream(ims, null)
                            view.flights_imageView_return.setImageDrawable(d)
                        } catch (ex: IOException) {
                            //file does not exist
                            view.flights_imageView_return.setImageResource(R.drawable.ic_flight_black_24dp)
                        } finally {
                            ims?.close()
                        }

                        //view.flights_imageView_return.setImageResource(R.drawable.ic_flight_black_24dp)

                    }
                    if (flights[pot].retour == 0 && (pot == flights.size - 1 || flights[pot + 1].dureeVol != flights[pot].dureeVol)) {
                        view.heureArrivee_aller.text =
                            Flight.heureArrivee
                        view.lieuArrivee_aller.text = "${Flight.lieuArrivee}, "

                        val parsedDateDepart =
                            SimpleDateFormat("dd/MM/yyyy").parse(flights[0].dateDepart)
                        val parsedDateArrivee =
                            SimpleDateFormat("dd/MM/yyyy").parse(Flight.dateArrivee)

                        val diff = parsedDateArrivee!!.time.minus(parsedDateDepart!!.time)

                        val compare = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                        if (compare.toInt() != 0) {
                            view.heureArriveeAdd_aller.text = "+${compare}"
                            view.heureArriveeAdd_aller.setTextColor(ContextCompat
                                .getColor(view.heureArriveeAdd_aller.context, R.color.colorAccent))
                            view.heureArrivee_aller.setTextColor(ContextCompat
                                .getColor(view.heureArrivee_aller.context, R.color.colorAccent))
                        }
                        if (pot == flights.size - 1) view.layout_retour.visibility =
                            View.GONE
                    }
                    if (flights[pot].retour == 1 && pot == flights.size - 1) {
                        view.heureArrivee_retour.text =
                            Flight.heureArrivee
                        view.lieuArrivee_retour.text = "${Flight.lieuArrivee}, "

                        val parsedDateDepart =
                            SimpleDateFormat("dd/MM/yyyy").parse(flights[returnTravel].dateDepart)
                        val parsedDateArrivee =
                            SimpleDateFormat("dd/MM/yyyy").parse(Flight.dateArrivee)

                        val diff = parsedDateArrivee!!.time.minus(parsedDateDepart!!.time)

                        val compare = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                        if (compare.toInt() != 0) {
                            view.heureArriveeAdd_retour.text = "+${compare}"
                            view.heureArriveeAdd_retour.setTextColor(ContextCompat
                                .getColor(view.heureArriveeAdd_retour.context, R.color.colorAccent))
                            view.heureArrivee_retour.setTextColor(ContextCompat
                                .getColor(view.heureArrivee_retour.context, R.color.colorAccent))
                        }
                    }
                    pot += 1

                }


            }
            view.setOnClickListener { itView ->
    //            Log.d("EPF", "$flights")
                val intent = Intent(itView.context, DetailFlights::class.java)
                //    intent.putExtra("lastname", client.lastname)

                intent.putExtra("flights", flights.toTypedArray())
                itView.context.startActivity(intent)
            }

        }

    }
}