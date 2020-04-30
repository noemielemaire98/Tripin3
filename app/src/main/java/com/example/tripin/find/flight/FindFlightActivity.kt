package com.example.tripin.find.flight

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.resources.FlightOfferSearch
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.model.Flight
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.android.synthetic.main.activity_find_flight.*
import kotlinx.android.synthetic.main.activity_find_flight.aller_date
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.O)
class FindFlightActivity : AppCompatActivity() {

    //    private val apiKey = "TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs"
//    private val apiSecret = "a16JGxtWdWBPtTGB"
    private lateinit var dateDepart: String
    private lateinit var dateRetour: String
    private lateinit var lieuDepart: String
    private lateinit var lieuRetour: String
    private var cal: Calendar = Calendar.getInstance()
    private var flightDao: FlightDao? = null
    private lateinit var flightsRecyclerView: RecyclerView

    //    private var listFlights = mutableListOf<Flight>()
    private var activityCreate = true
    private val amadeus: Amadeus = Amadeus
        .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
        .build()


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_flight)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        flightsRecyclerView =
            findViewById<View>(R.id.flights_recyclerview) as RecyclerView
        flightsRecyclerView.layoutManager = LinearLayoutManager(this)

        initDatePicker(aller_date)
        initDatePicker(return_date)


        runBlocking {
            val airportCsv = resources.openRawResource(R.raw.iata_airport_list)
            val listAirports: List<Map<String, String>> = csvReader().readAllWithHeader(airportCsv)


            val listAirportsFormatted = mutableListOf<String>()

            listAirports.map { itMap ->
                listAirportsFormatted.add(
                    itMap["city_name"].toString()
                        .toUpperCase(Locale.ROOT) + " " + itMap["por_name"].toString()
                        .toUpperCase(Locale.ROOT) + " (" + itMap["por_code"].toString().toUpperCase(
                        Locale.ROOT
                    ) + ")"
                )
            }

            val adapter =
                IgnoreAccentsArrayAdapter(
                    this@FindFlightActivity,
                    android.R.layout.simple_list_item_1, listAirportsFormatted.toTypedArray()
                )
            autoTextViewDepart.setAdapter(adapter)
            autoTextViewRetour.setAdapter(adapter)
        }
        //empty autoTextView if not chosen from list
        clearFocusAutoTextView(autoTextViewDepart)
        clearFocusAutoTextView(autoTextViewRetour)

        btn_search.setOnClickListener {
            hideKeyboard()
            savedTopLevel_layout.requestFocus()
            if (autoTextViewDepart.text.toString() == "" || autoTextViewRetour.text.toString() == "") {
                Toast.makeText(
                    this,
                    "Veuillez choisir les destinations dans les listes déroulantes",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val strDepart = autoTextViewDepart.text.toString()
            val keptDepart = strDepart.substringAfterLast("(")
            lieuDepart = keptDepart.substringBeforeLast(")")
            val strRetour = autoTextViewRetour.text.toString()
            val keptRetour = strRetour.substringAfterLast("(")
            lieuRetour = keptRetour.substringBeforeLast(")")

            if (aller_date.text.toString() != "" && return_date.text.toString() != "") {
                val parsedDateDepart =
                    SimpleDateFormat("yyyy-MM-dd").parse(aller_date.text.toString())
                val parsedDateRetour =
                    SimpleDateFormat("yyyy-MM-dd").parse(return_date.text.toString())

                if (parsedDateDepart!!.before(parsedDateRetour)) {

                    dateRetour = return_date.text.toString()
                    beginSearchExported(dateRetour)

                } else {
                    Toast.makeText(
                        this,
                        "La date de retour doit être ultérieure à celle de départ",
                        Toast.LENGTH_SHORT
                    ).show()
                    return_date.setText("")
                }
            } else if (aller_date.text.toString() != "" && return_date.visibility == View.GONE) {
                dateRetour = ""
                beginSearchExported(dateRetour)
            } else {
                Toast.makeText(
                    this,
                    "Veuillez remplir tous les champs",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

        allerType_radiogroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            if (radio.id == simple_button.id) {
                return_date.visibility = View.GONE
                return_date.setText("")
            } else {
                return_date.visibility = View.VISIBLE
            }
        }


        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "searchDatabase")
                .build()

        flightDao = database.getFlightDao()


    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            //       val flights = listFlights
            val flights = flightDao?.getFlights()
            if (!flights.isNullOrEmpty()) {
                layoutNoSavedFlight.visibility = View.GONE
                //            val flights = listFlights
                val flightsList: MutableList<MutableList<Flight>> = mutableListOf()
                var testlist: MutableList<Flight> = mutableListOf()
                var travelId = 1
                var position = 0
                flights.map {
                    val flight = flights[it.id - 1]
                    if (it.travelId != travelId) {
                        flightsList.add(testlist)
                        testlist = mutableListOf()
                        travelId = it.travelId

                    }
                    testlist.add(flight)
                    position += 1
                    if (flights.size == it.id) {
                        flightsList.add(testlist)
                    }
                }
                flightsRecyclerView.adapter =
                    FlightsAdapter(flightsList)
            } else if (!activityCreate) {
                layoutNoSavedFlight.visibility = View.VISIBLE
            }
            loadingPanel.visibility = View.GONE
        }
    }

    private fun beginSearchExported(dateRetour: String) {
        dateDepart = aller_date.text.toString()

        val nbAdults = spinner.selectedItem.toString().toInt()
        flights_recyclerview.adapter =
            FlightsAdapter(mutableListOf())
        layout_search.visibility = View.GONE
        loadingPanel.visibility = View.VISIBLE
        layoutNoSavedFlight.visibility = View.GONE
        runBlocking {
            beginSearch(dateDepart, dateRetour, lieuDepart, lieuRetour, nbAdults)
        }
        activityCreate = false
    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private suspend fun beginSearch(
        dateDepart: String,
        dateRetour: String,
        lieuDepart: String,
        lieuRetour: String,
        nbAdults: Int
    ) {
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        flightDao?.deleteFlights()

        scope.launch {



                val flightOffersSearches: Array<FlightOfferSearch>
                if (dateRetour != "") {
                    flightOffersSearches =
                        amadeus.shopping.flightOffersSearch[Params
                            .with("originLocationCode", lieuDepart)
                            .and("destinationLocationCode", lieuRetour)
                            .and("departureDate", dateDepart)
                            .and("returnDate", dateRetour)
                            .and("adults", nbAdults)
                            .and("max", 10)]
                } else {
                    flightOffersSearches =
                        amadeus.shopping.flightOffersSearch[Params
                            .with("originLocationCode", lieuDepart)
                            .and("destinationLocationCode", lieuRetour)
                            .and("departureDate", dateDepart)
                            .and("adults", nbAdults)
                            .and("max", 10)]
                }

                withContext(Dispatchers.Main) {
                    //      runOnUiThread {

                    val test = resources.openRawResource(R.raw.optd_airline)
                    val rows: List<Map<String, String>> = csvReader().readAllWithHeader(test)

//                val testLogo = resources.openRawResource(R.raw.optd_airlines_websites_wkdt)
//                val rowsLogo: List<Map<String, String>> = csvReader().readAllWithHeader(testLogo)

                    var id = 1
                    var valueCa = ""
                    var valueLogo = ""
                    val flightsList: MutableList<MutableList<Flight>> = mutableListOf()

                    flightOffersSearches.map { itFlight ->
                        var retour = 0
                        var uuidSomme = ""
                        val listFlightAdd = mutableListOf<Flight>()
                        itFlight.itineraries.map { itItineraries ->
                            val nbEscales = itItineraries.segments.size - 1
                            itItineraries.segments.map { itSegments ->

                                val sec = Duration.parse(itItineraries.duration).seconds
                                val dureeFormat = String.format(
                                    "%02dh%02d", TimeUnit.SECONDS.toHours(sec),
                                    TimeUnit.SECONDS.toMinutes(sec) % TimeUnit.HOURS.toMinutes(1)
                                )

                                val departureDate = itSegments.departure.at
                                val arrivalDate = itSegments.arrival.at

                                val parser =
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                val formatterHour =
                                    SimpleDateFormat("HH:mm")
                                val formatterDate =
                                    SimpleDateFormat("dd/MM/yyyy")
                                val dateDepartFormat =
                                    formatterDate.format(parser.parse(departureDate)!!)
                                val dateArriveeFormat =
                                    formatterDate.format(parser.parse(arrivalDate)!!)
                                val heureDepartFormat =
                                    formatterHour.format(parser.parse(departureDate)!!)
                                val heureArriveeFormat =
                                    formatterHour.format(parser.parse(arrivalDate)!!)


                                rows.map { itMap ->
                                    itMap.map {
                                        if (it.value == itSegments.carrierCode) {
                                            valueCa = itMap["name"].toString()
                                            valueLogo = itMap["2char_code"].toString()
                                        }
                                    }
                                }

                                val uuid = itSegments.carrierCode + itSegments.number

                                val flight = Flight(
                                    id,
                                    itFlight.id.toInt(),
                                    itSegments.id.toInt(),
                                    itFlight.price.grandTotal,
                                    itFlight.travelerPricings[0].price.total,
                                    dateDepartFormat,
                                    heureDepartFormat,
                                    dateArriveeFormat,
                                    heureArriveeFormat,
                                    dureeFormat,
                                    itSegments.departure.iataCode,
                                    itSegments.arrival.iataCode,
                                    itSegments.carrierCode,
                                    valueLogo,
                                    valueCa,
                                    nbEscales,
                                    retour,
                                    "optd_airlines_websites_logos/${valueLogo.toLowerCase(
                                        Locale.ROOT
                                    )}.png",
                                    false,
                                    uuid
                                )
                                uuidSomme += uuid
                                listFlightAdd.add(flight)

                                id += 1
                            }


                            retour += 1
                        }
                        listFlightAdd.map { itListF ->
                            itListF.uuid = uuidSomme
                            flightDao?.addFlight(itListF)
                        }
                        flightsList.add(listFlightAdd)

                    }
                    if (!flightsList.isNullOrEmpty()) {
                        layoutNoSavedFlight.visibility = View.GONE
                        flightsRecyclerView.adapter =
                            FlightsAdapter(
                                flightsList
                            )
                    } else if (!activityCreate) {
                        layoutNoSavedFlight.visibility = View.VISIBLE
                    }

                    loadingPanel.visibility = View.GONE
                }
            }

    }


    private fun clearFocusAutoTextView(autoCompleteTextView: AutoCompleteTextView) {
        autoCompleteTextView.onFocusChangeListener = OnFocusChangeListener { _, b ->
            if (!b) {
                // on focus off
                val str: String = autoCompleteTextView.text.toString()
                val listAdapter: ListAdapter = autoCompleteTextView.adapter
                for (i in 0 until listAdapter.count) {
                    val temp: String = listAdapter.getItem(i).toString()
                    if (str.compareTo(temp) == 0) {
                        return@OnFocusChangeListener
                    }
                }
                autoCompleteTextView.setText("")
            }
        }
    }

    private fun initDatePicker(text: EditText) {
        val startDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                text.setText(sdf.format(cal.time))
            }
        text.setOnClickListener {
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

//    private fun Fragment.hideKeyboard() {
//        view?.let { activity?.hideKeyboard(it) }
//    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
