package com.example.tripin.ui.find

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
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
import java.util.*


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
        view.start_date?.setOnClickListener {
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

            if (start_date.text.toString() != "" && return_date.text.toString() != "") {
                val parsedDateDepart =
                    SimpleDateFormat("yyyy-MM-dd").parse(start_date.text.toString())
                val parsedDateRetour =
                    SimpleDateFormat("yyyy-MM-dd").parse(return_date.text.toString())
                if (parsedDateDepart!!.before(parsedDateRetour)) {

                    dateDepart = start_date.text.toString()
                    dateRetour = return_date.text.toString()
                    flights_recyclerview.adapter = FlightsAdapter(emptyList())
                    loadingPanel.visibility = View.VISIBLE;
                    beginSearch(dateDepart, dateRetour)

                } else {
                    alert("La date de retour doit être ultérieure à celle de départ.") {
                        title = "Alert"
                        yesButton {}
                    }.show()
                    return_date.setText("")
                }
            } else {
                alert("Veuillez remplir tous les champs.") {
                    title = "Alert"
                    yesButton {}
                }.show()
            }
        }

        view.allerType_radiogroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener
            { group, checkedId ->
                val radio: RadioButton = activity!!.findViewById(checkedId)
                view.return_date.visibility =
                    if (view.allerType_radiogroup.checkedRadioButtonId == view.simple_button.id) {
                        View.GONE
                    } else {
                        View.VISIBLE
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
            //     val flight = Flight(1, 20.toDouble(), "DepartureDate")
            flights_recyclerview.adapter = FlightsAdapter(flights ?: emptyList())
            loadingPanel.visibility = View.GONE;

        }
    }

    @SuppressLint("SetTextI18n")
    private fun beginSearch(dateDepart: String, dateRetour: String) {
        runBlocking {
            flightDao?.deleteFlights()

            val amadeus = Amadeus
                .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
                .build()

// Your kotlin Coroutine scope
            GlobalScope.launch {
                val flightOffersSearches =
                    amadeus.shopping.flightOffersSearch[Params.with("originLocationCode", "SYD")
                        .and("destinationLocationCode", "BKK")
                        .and("departureDate", dateDepart)
                        .and("returnDate", dateRetour)
                        .and("adults", 2)
                        .and("max", 3)]

                activity!!.runOnUiThread(java.lang.Runnable {

                    //           Log.d("Flights", flightOffersSearches.contentToString())
                    var id = 1
                    flightOffersSearches.map { it ->

                        it.itineraries.map { ot ->
                            ot.segments.map { ut ->
                                val flight = Flight(
                                    id,
                                    it.price.grandTotal,
                                    ut.departure.at,
                                    it.isOneWay
                                )

                                runBlocking {
                                    flightDao?.addFlight(flight)
                                }
                                id += 1
                            }
                        }
                    }
                    onResume()
                })
            }

        }


    }

    private fun updateStartDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        start_date.setText(sdf.format(cal.time))
    }

    private fun updateReturnDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return_date.setText(sdf.format(cal.time))
    }

}


fun Boolean.toDirect() = if (this) "Vol direct" else "Vol avec escale(s)"
