package com.example.tripin.find.flight

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Flight
import com.example.tripin.model.Voyage
import com.example.tripin.saved.SavedFlight
import com.example.tripin.trip.DetailVoyage2
import kotlinx.android.synthetic.main.activity_saved_flight.*
import kotlinx.android.synthetic.main.flightstrip_view.view.*
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class FlightsAdapterTrip(
    private val flightsList: MutableList<MutableList<Flight>>,
    private val voyage: Voyage
) :
    RecyclerView.Adapter<FlightsAdapterTrip.FlightsViewHolder>() {

    private var voyageDaoSaved: VoyageDao? = null
    private var listFlightsBdd: MutableList<Flight>? = null
    private var flightDaoSaved: FlightDao? = null
    private lateinit var context: Context

    class FlightsViewHolder(val flightsView: View) : RecyclerView.ViewHolder(flightsView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightsViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.flightstrip_view, parent, false)

        context = parent.context

        val databaseSaved = Room.databaseBuilder(context, AppDatabase::class.java, "savedDatabase")
            .build()

        flightDaoSaved = databaseSaved.getFlightDao()
        voyageDaoSaved = databaseSaved.getVoyageDao()

        runBlocking {
            listFlightsBdd = flightDaoSaved?.getFlights()
        }
        return FlightsViewHolder(view)
    }

    override fun getItemCount(): Int = flightsList.size


    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: FlightsViewHolder, position: Int) {
        val view = holder.flightsView

        var favoris = false
        flightsList.map { itListFlight ->

            val flights = flightsList[position]
            var pot = 0
            var returnTravel = 0


            itListFlight.map {
                if (pot != flights.size) {
                    val itFlight = flights[pot]
                    if (flights[pot].retour == 0 && (pot == 0
                                || (flights[pot - 1].travelId == flights[pot].travelId
                                && flights[pot - 1].dureeVol != flights[pot].dureeVol))
                    ) {

                        view.flights_price_textview.text = "${itFlight.prixTotal} €"
                        view.aller_date.text = "${itFlight.heureDepart} - "
                        view.flights_date_textview.text = itFlight.dateDepart

                        view.lieuDepart_aller.text = "${itFlight.lieuDepart} - "

                        view.compagnie_aller.text =
                            if (itFlight.carrierName != "") itFlight.carrierName else itFlight.carrierCode

                        view.duree.text = itFlight.dureeVol
                        view.flights_direct_textview.text =
                            if (itFlight.nbEscales == 1) "${itFlight.nbEscales} escale" else if (itFlight.nbEscales > 1) "${itFlight.nbEscales} escales" else "Direct"

                        var ims: InputStream? = null
                        try {
                            ims = view.context.assets.open(
                                "optd_airlines_websites_logos/${itFlight.carrierCodeLogo.toLowerCase(
                                    Locale.ROOT
                                )}.png"
                            )
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

                        view.heureDepart_retour.text = "${itFlight.heureDepart} - "
                        view.flights_date_return_textview.text = itFlight.dateDepart

                        view.lieuDepart_retour.text = "${itFlight.lieuDepart} - "

                        view.compagnie_retour.text =
                            if (itFlight.carrierName != "") itFlight.carrierName else itFlight.carrierCode

                        view.duree_retour.text = itFlight.dureeVol
                        view.flights_direct_return_textview.text =
                            if (itFlight.nbEscales == 1) "${itFlight.nbEscales} escale" else if (itFlight.nbEscales > 1) "${itFlight.nbEscales} escales" else "Direct"

                        var ims: InputStream? = null
                        try {
                            ims = view.context.assets.open(
                                "optd_airlines_websites_logos/${itFlight.carrierCodeLogo.toLowerCase(
                                    Locale.ROOT
                                )}.png"
                            )
                            val d = Drawable.createFromStream(ims, null)
                            view.flights_imageView_return.setImageDrawable(d)
                        } catch (ex: IOException) {
                            //file does not exist
                            view.flights_imageView_return.setImageResource(R.drawable.ic_flight_black_24dp)
                        } finally {
                            ims?.close()
                        }

                    }
                    if (flights[pot].retour == 0 && (pot == flights.size - 1 || flights[pot + 1].dureeVol != flights[pot].dureeVol)) {
                        view.heureArrivee_aller.text =
                            itFlight.heureArrivee
                        view.lieuArrivee_aller.text = "${itFlight.lieuArrivee}, "

                        val parsedDateDepart =
                            SimpleDateFormat("dd/MM/yyyy").parse(flights[0].dateDepart)
                        val parsedDateArrivee =
                            SimpleDateFormat("dd/MM/yyyy").parse(itFlight.dateArrivee)

                        val diff = parsedDateArrivee!!.time.minus(parsedDateDepart!!.time)

                        val compare = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                        if (compare.toInt() != 0) {
                            view.heureArriveeAdd_aller.text = "+${compare}"
                            view.heureArriveeAdd_aller.setTextColor(
                                ContextCompat
                                    .getColor(
                                        view.heureArriveeAdd_aller.context,
                                        R.color.colorAccent
                                    )
                            )
                            view.heureArrivee_aller.setTextColor(
                                ContextCompat
                                    .getColor(
                                        view.heureArrivee_aller.context,
                                        R.color.colorAccent
                                    )
                            )
                        }
                        if (pot == flights.size - 1) view.layout_retour.visibility =
                            View.GONE
                    }
                    if (flights[pot].retour == 1 && pot == flights.size - 1) {
                        view.heureArrivee_retour.text =
                            itFlight.heureArrivee
                        view.lieuArrivee_retour.text = "${itFlight.lieuArrivee}, "

                        val parsedDateDepart =
                            SimpleDateFormat("dd/MM/yyyy").parse(flights[returnTravel].dateDepart)
                        val parsedDateArrivee =
                            SimpleDateFormat("dd/MM/yyyy").parse(itFlight.dateArrivee)

                        val diff = parsedDateArrivee!!.time.minus(parsedDateDepart!!.time)

                        val compare = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                        if (compare.toInt() != 0) {
                            view.heureArriveeAdd_retour.text = "+${compare}"
                            view.heureArriveeAdd_retour.setTextColor(
                                ContextCompat
                                    .getColor(
                                        view.heureArriveeAdd_retour.context,
                                        R.color.colorAccent
                                    )
                            )
                            view.heureArrivee_retour.setTextColor(
                                ContextCompat
                                    .getColor(
                                        view.heureArrivee_retour.context,
                                        R.color.colorAccent
                                    )
                            )
                        }
                    }
                    pot += 1
                }
            }

            view.setOnClickListener { itView ->
                val intent = Intent(itView.context, DetailFlights::class.java)
                intent.putExtra("flights", flights.toTypedArray())
                itView.context.startActivity(intent)
            }

            listFlightsBdd?.map { itList ->

                if (itList.uuid == flights[0].uuid) {
                    favoris = true
                }
            }

            if (flights[0].favoris || favoris) {
                view.fab_favFlight.setImageResource(R.drawable.ic_favorite_black_24dp)
                favoris = true

            } else if (!flights[0].favoris || !favoris) {
                view.fab_favFlight.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                favoris = false

            }


            view.fab_favFlight.setOnClickListener {

                if (!favoris) {

                    val row = listFlightsBdd?.size
                    val travelId =
                        if (!listFlightsBdd.isNullOrEmpty()) listFlightsBdd!![row!! - 1].travelId + 1 else 1
                    var id =
                        if (!listFlightsBdd.isNullOrEmpty()) listFlightsBdd!![row!! - 1].id + 1 else 1
                    flights.map {
                        it.favoris = true
                        runBlocking {
                            val flight = Flight(
                                id,
                                travelId,
                                it.SegmentId,
                                it.prixTotal,
                                it.prixParPassager,
                                it.dateDepart,
                                it.heureDepart,
                                it.dateArrivee,
                                it.heureArrivee,
                                it.dureeVol,
                                it.lieuDepart,
                                it.lieuArrivee,
                                it.carrierCode,
                                it.carrierCodeLogo,
                                it.carrierName,
                                it.nbEscales,
                                it.retour,
                                it.favoris,
                                it.uuid
                            )
                            flightDaoSaved?.addFlight(flight)
                            id += 1
                        }
                    }
                    favoris = true
                    view.fab_favFlight.setImageResource(R.drawable.ic_favorite_black_24dp)
                    Toast.makeText(
                        context,
                        "Le vol a bien été ajouté aux favoris",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (favoris) {

                    flights.map {
                        runBlocking {
                            flightDaoSaved?.deleteFlight(it.uuid)
                        }
                    }

                    if (context.javaClass == SavedFlight::class.java) {
                        flightsList.remove(flights)

                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, itemCount)

                        if (flightsList.isNullOrEmpty()) {
                            (context as SavedFlight).layoutNoSavedFlight.visibility = View.VISIBLE
                        }
                    }

                    favoris = false

                    view.fab_favFlight.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    Toast.makeText(
                        context,
                        "Le vol a bien été supprimé des favoris",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                runBlocking {
                    listFlightsBdd = flightDaoSaved?.getFlights()
                }
            }

            view.fab_deleteFlight.setOnClickListener {
                flightsList.removeAt(position)
                val list = mutableListOf<Flight>()
                flightsList.map { itList ->
                    itList.map {
                        list.add(it)
                    }
                }
                voyage.list_flights = list.toList()
                runBlocking {
                    voyageDaoSaved?.updateVoyage(voyage)
                }
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)

                if (flightsList.isNullOrEmpty()) {
                    (context as DetailVoyage2).layoutNoSavedFlight.visibility = View.VISIBLE
                }
            }
        }
    }
}