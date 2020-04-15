package com.example.tripin.ui.find

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.resources.FlightOfferSearch
import com.example.tripin.FlightsAdapter
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.model.Flight
import kotlinx.android.synthetic.main.fragment_find.*
import kotlinx.android.synthetic.main.fragment_find.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class FindFragment : Fragment() {

    private val apiKey = "TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs"
    private val apiSecret = "a16JGxtWdWBPtTGB"
    private lateinit var dateDepart: String
    private lateinit var dateRetour: String
    private var cal: Calendar = Calendar.getInstance()
    private var flightDao: FlightDao? = null


    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_find, container, false)


        val flightsRecyclerview =
            view.findViewById<View>(R.id.flights_recyclerview) as RecyclerView
        flightsRecyclerview.layoutManager = LinearLayoutManager(this.context)

        val startDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateStartDateInView()
            }

        val returnDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateReturnDateInView()
            }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        view.heures_aller?.setOnClickListener {
            val dialogDepart = DatePickerDialog(
                requireContext(),
                startDateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dialogDepart.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialogDepart.show()
        }

        view.return_date?.setOnClickListener {
            val dialogRetour = DatePickerDialog(
                requireContext(),
                returnDateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dialogRetour.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialogRetour.show()
        }


        view.btn_search.setOnClickListener {

            if (heures_aller.text.toString() != "" && return_date.text.toString() != "") {
                val parsedDateDepart =
                    SimpleDateFormat("yyyy-MM-dd").parse(heures_aller.text.toString())
                val parsedDateRetour =
                    SimpleDateFormat("yyyy-MM-dd").parse(return_date.text.toString())
                if (parsedDateDepart!!.before(parsedDateRetour)) {

                    dateDepart = heures_aller.text.toString()
                    dateRetour = return_date.text.toString()
                    flights_recyclerview.adapter = FlightsAdapter(mutableListOf())
                    loadingPanel.visibility = View.VISIBLE;
                    beginSearch(dateDepart, dateRetour)

                } else {
                    alert("La date de retour doit être ultérieure à celle de départ.") {
                        title = "Alert"
                        yesButton {}
                    }.show()
                    return_date.setText("")
                }
            } else if (heures_aller.text.toString() != "" && return_date.visibility == View.GONE) {
                dateDepart = heures_aller.text.toString()
                dateRetour = ""
                flights_recyclerview.adapter = FlightsAdapter(mutableListOf())
                loadingPanel.visibility = View.VISIBLE;
                beginSearch(dateDepart, dateRetour)
            } else {
                alert("Veuillez remplir tous les champs.") {
                    title = "Alert"
                    yesButton {}
                }.show()
            }
        }

        view.allerType_radiogroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener
            { _, checkedId ->
                val radio: RadioButton = activity!!.findViewById(checkedId)
                if (view.allerType_radiogroup.checkedRadioButtonId == view.simple_button.id) {
                    view.return_date.visibility = View.GONE
                    view.return_date.setText("")
                } else {
                    view.return_date.visibility = View.VISIBLE
                }
            })


        val database =
            Room.databaseBuilder(this.context!!, AppDatabase::class.java, "gestionflights")
                .build()

        flightDao = database.getFlightDao()

        return view

    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val flights = flightDao?.getFlights()
            var flightslist: MutableList<MutableList<Flight>> = mutableListOf()
            var testlist: MutableList<Flight> = mutableListOf()
            var travelId = 1
            var position = 0
            flights?.map {
                var Flight = flights[it.id - 1]
                if (it.travelId != travelId) {
                    flightslist.add(testlist)
                    testlist = mutableListOf()
                    travelId = it.travelId

                }
                testlist.add(Flight)
                position += 1
                if (flights.size == it.id) {
                    flightslist.add(testlist)
                }


            }
            //       Log.d("List", flightslist.toString())
            //     val flight = Flight(1, 20.toDouble(), "DepartureDate")
            flights_recyclerview.adapter = FlightsAdapter(flightslist)
            loadingPanel.visibility = View.GONE;

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun beginSearch(dateDepart: String, dateRetour: String) {
        runBlocking {
            flightDao?.deleteFlights()
        }

        val amadeus = Amadeus
            .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
            .build()

// Your kotlin Coroutine scope
        GlobalScope.launch {
            var flightOffersSearches = emptyArray<FlightOfferSearch>()
            if (dateRetour != "") {
                flightOffersSearches =
                    amadeus.shopping.flightOffersSearch[Params.with("originLocationCode", "SYD")
                        .and("destinationLocationCode", "BKK")
                        .and("departureDate", dateDepart)
                        .and("returnDate", dateRetour)
                        .and("adults", 2)
                        .and("max", 10)]
            } else {
                flightOffersSearches =
                    amadeus.shopping.flightOffersSearch[Params.with("originLocationCode", "SYD")
                        .and("destinationLocationCode", "BKK")
                        .and("departureDate", dateDepart)
                        .and("adults", 2)
                        .and("max", 10)]
            }

            activity!!.runOnUiThread(java.lang.Runnable {

                //           Log.d("Flights", flightOffersSearches.contentToString())
                var id = 1
                var retourValue = 1
                flightOffersSearches.map { itFlight ->
                    var retour = 0
                    itFlight.itineraries.map { itItineraries ->
                        var nbEscales = itItineraries.segments.size - 1
                        itItineraries.segments.map { itSegments ->
                            val sec = Duration.parse(itItineraries.duration).seconds
                            val dureeFormat = String.format(
                                "%02dh%02d", TimeUnit.SECONDS.toHours(sec),
                                TimeUnit.SECONDS.toMinutes(sec) % TimeUnit.HOURS.toMinutes(1)
                            )
                            val test = itSegments.departure.at
                            val parser =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            val formatter =
                                SimpleDateFormat("dd/MM/yyyy 'à' HH:mm")
                            val output =
                                formatter.format(parser.parse(itSegments.departure.at)!!)

                            val flight = Flight(
                                id,
                                itFlight.id.toInt(),
                                itSegments.id.toInt(),
                                itFlight.price.grandTotal,
                                output,
                                dureeFormat,
                                itFlight.isOneWay,
                                nbEscales,
                                retour
                            )

                            runBlocking {
                                flightDao?.addFlight(flight)
                            }
                            id += 1
                        }
                        retour += 1
                        retourValue = id
                    }
                }
                onResume()
            })
        }
    }

    private fun updateStartDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        heures_aller.setText(sdf.format(cal.time))
    }

    private fun updateReturnDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return_date.setText(sdf.format(cal.time))
    }

}


fun Boolean.toDirect() = if (this) "Vol direct" else "Vol avec escale(s)"
