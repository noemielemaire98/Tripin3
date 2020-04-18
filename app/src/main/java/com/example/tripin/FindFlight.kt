package com.example.tripin

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.referenceData.Locations
import com.amadeus.resources.FlightOfferSearch
import com.amadeus.resources.Location
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.data.LocationDao
import com.example.tripin.model.Flight
import com.example.tripin.model.LocationList
import kotlinx.android.synthetic.main.activity_find_flight.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.O)
class FindFlight : AppCompatActivity() {

    private val apiKey = "TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs"
    private val apiSecret = "a16JGxtWdWBPtTGB"
    private lateinit var dateDepart: String
    private lateinit var dateRetour: String
    private var cal: Calendar = Calendar.getInstance()
    private var flightDao: FlightDao? = null
    private var locationDao: LocationDao? = null
    private var listFlights = mutableListOf<Flight>()
    private val amadeus: Amadeus = Amadeus
        .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
        .build()


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_flight)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val flightsRecyclerview =
            findViewById<View>(R.id.flights_recyclerview) as RecyclerView
        flightsRecyclerview.layoutManager = LinearLayoutManager(this)

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
        heures_aller?.setOnClickListener {
            val dialogDepart = DatePickerDialog(
                this,
                startDateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dialogDepart.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialogDepart.show()
        }

        return_date?.setOnClickListener {
            val dialogRetour = DatePickerDialog(
                this,
                returnDateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dialogRetour.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialogRetour.show()
        }

        runBlocking {
            locationDao?.deleteLocation()
        }
        val autotextView = findViewById<AutoCompleteTextView>(R.id.autoTextViewDepart)


        var locations = emptyArray<Location?>()
        var testo = true
        autotextView.afterTextChanged { it ->
            if (testo && it.length == 1) {
                GlobalScope.launch {
                    locations = amadeus.referenceData.locations[Params
                        .with("keyword", it)
                        .and("subType", Locations.AIRPORT)
                        .and("view", "LIGHT")
                        .and("page[limit]", 10000)]

                    runOnUiThread(java.lang.Runnable {
                        val listville = mutableListOf<String>()

                        runBlocking {
                            locationDao?.deleteLocation()
                        }
                        var id = 1
                        locations.map { itLoc ->
                            val test = itLoc?.iataCode
                            listville.add(test.toString())
                            val loc = LocationList(id, test.toString())

                            runBlocking {
                                locationDao?.addLocation(loc)
                            }
                            id += 1
                        }

                        runBlocking {
                            val loco = locationDao?.getLocation()
                            val listString = mutableListOf<String>()
                            loco?.map { itLou ->
                                listString.add(itLou.name)
                            }
                            runOnUiThread(java.lang.Runnable {
                                val adapter = ArrayAdapter(
                                    this@FindFlight,
                                    android.R.layout.simple_list_item_1, listString
                                )
                                autotextView.setAdapter(adapter)
                            })
                        }
                    })
                    testo = false
                }
            } else if (!testo && it.length < 2) {
                runBlocking {
                    locationDao?.deleteLocation()
                }
                testo = true
            }

        }


//        button_newSearch.setOnClickListener {
//            if (layout_search.visibility == View.VISIBLE)
//                layout_search.visibility = View.GONE
//            else {
//                layout_search.visibility = View.VISIBLE
//                button_newSearch.visibility = View.GONE
//            }
//        }


        btn_search.setOnClickListener {

            if (heures_aller.text.toString() != "" && return_date.text.toString() != "") {
                val parsedDateDepart =
                    SimpleDateFormat("yyyy-MM-dd").parse(heures_aller.text.toString())
                val parsedDateRetour =
                    SimpleDateFormat("yyyy-MM-dd").parse(return_date.text.toString())
                if (parsedDateDepart!!.before(parsedDateRetour)) {

                    dateDepart = heures_aller.text.toString()
                    dateRetour = return_date.text.toString()
                    val lieuDepart = autotextView.text.toString()
                    val nbAdults = spinner.selectedItem.toString().toInt()
                    flights_recyclerview.adapter = FlightsAdapter(mutableListOf())
                    loadingPanel.visibility = View.VISIBLE
                    beginSearch(dateDepart, dateRetour, lieuDepart, "BKK", nbAdults)
                    layout_search.visibility = View.GONE
                    //             button_newSearch.visibility = View.VISIBLE

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
                val lieuDepart = autotextView.text.toString()
                val nbAdults = spinner.selectedItem.toString().toInt()
                flights_recyclerview.adapter = FlightsAdapter(mutableListOf())
                loadingPanel.visibility = View.VISIBLE
                beginSearch(dateDepart, dateRetour, lieuDepart, "BKK", nbAdults)
                layout_search.visibility = View.GONE
                //           button_newSearch.visibility = View.VISIBLE
            } else {
                alert("Veuillez remplir tous les champs.") {
                    title = "Alert"
                    yesButton {}
                }.show()
            }
        }

        allerType_radiogroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener
            { _, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                if (radio.id == simple_button.id) {
                    return_date.visibility = View.GONE
                    return_date.setText("")
                } else {
                    return_date.visibility = View.VISIBLE
                }
            })


        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "gestionflights")
                .build()

        flightDao = database.getFlightDao()
        locationDao = database.getLocationDao()

    }

    override fun onResume() {
        super.onResume()

        runBlocking {
  val flights = flightDao?.getFlights()
//            val flights = listFlights
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
            Log.d("List", flightslist.toString())
            //     val flight = Flight(1, 20.toDouble(), "DepartureDate")
            flights_recyclerview.adapter = FlightsAdapter(flightslist)
            loadingPanel.visibility = View.GONE;

        }
    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun beginSearch(
        dateDepart: String,
        dateRetour: String,
        lieuDepart: String,
        lieuRetour: String,
        nbAdults: Int
    ) {
        runBlocking {
            flightDao?.deleteFlights()
        }


// Your kotlin Coroutine scope
        GlobalScope.launch {
            var flightOffersSearches: Array<FlightOfferSearch>
            if (dateRetour != "") {
                flightOffersSearches =
                    amadeus.shopping.flightOffersSearch[Params.with("originLocationCode", "SYD")
                        .and("destinationLocationCode", "BKK")
                        .and("departureDate", dateDepart)
                        .and("returnDate", dateRetour)
                        .and("adults", nbAdults)
                        .and("max", 10)]
            } else {
                flightOffersSearches =
                    amadeus.shopping.flightOffersSearch[Params.with("originLocationCode", "PAR")
                        .and("destinationLocationCode", "BKK")
                        .and("departureDate", dateDepart)
                        .and("adults", nbAdults)
                        .and("max", 10)]
            }

            runOnUiThread(java.lang.Runnable {

                //           Log.d("Flights", flightOffersSearches.contentToString())
                var id = 1
                flightOffersSearches.map { itFlight ->
                    var retour = 0
                    itFlight.itineraries.map { itItineraries ->
                        val nbEscales = itItineraries.segments.size - 1
                        itItineraries.segments.map { itSegments ->
                            val sec = Duration.parse(itItineraries.duration).seconds
                            val dureeFormat = String.format(
                                "%02dh%02d", TimeUnit.SECONDS.toHours(sec),
                                TimeUnit.SECONDS.toMinutes(sec) % TimeUnit.HOURS.toMinutes(1)
                            )
                            val parser =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            val formatter =
                                SimpleDateFormat("dd/MM/yyyy 'à' HH:mm")
                            val dateDepart =
                                formatter.format(parser.parse(itSegments.departure.at)!!)
                            val dateArrivee =
                                formatter.format(parser.parse(itSegments.arrival.at)!!)

                            val flight = Flight(
                                id,
                                itFlight.id.toInt(),
                                itSegments.id.toInt(),
                                itFlight.price.grandTotal,
                                itFlight.price.grandTotal/nbAdults,
                                dateDepart,
                                dateArrivee,
                                dureeFormat,
                                itFlight.isOneWay,
                                nbEscales,
                                retour
                            )

                            runBlocking {
                                  flightDao?.addFlight(flight)
                              //  listFlights.add(flight)
                            }
                            id += 1
                        }
                        retour += 1
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

    private fun AutoCompleteTextView.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_find_flights, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_search -> {
                if (layout_search.visibility == View.VISIBLE) {
                    layout_search.visibility = View.GONE
                    //              button_newSearch.visibility = View.VISIBLE
                } else {
                    layout_search.visibility = View.VISIBLE
                    //              button_newSearch.visibility = View.GONE
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}


fun Boolean.toDirect() = if (this) "Vol direct" else "Vol avec escale(s)"
