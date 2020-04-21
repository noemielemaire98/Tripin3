package com.example.tripin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ListAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.resources.FlightOfferSearch
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.data.LocationDao
import com.example.tripin.model.Flight
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
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
        aller_date?.setOnClickListener {
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
            val test = resources.openRawResource(R.raw.iata_airport_list)
            val rows: List<Map<String, String>> = csvReader().readAllWithHeader(test)


            val listS = mutableListOf<String>()

            rows.map { itMap ->
                listS.add(
                    itMap["city_name"].toString().toUpperCase() + " " + itMap["por_name"].toString()
                        .toUpperCase() + " (" + itMap["por_code"].toString().toUpperCase() + ")"
                )
            }

            val adapter = IgnoreAccentsArrayAdapter(
                this@FindFlight,
                android.R.layout.simple_list_item_1, listS.toTypedArray()
            )
            autoTextViewDepart.setAdapter(adapter)
            autoTextViewRetour.setAdapter(adapter)
        }

        clearFocusAutoTextView(autoTextViewDepart)
        clearFocusAutoTextView(autoTextViewRetour)


//        runBlocking {
//            locationDao?.deleteLocation()
//        }
//        val autotextViewDepart = findViewById<AutoCompleteTextView>(R.id.autoTextViewDepart)
//
//
//        var locations: Array<Location?>
//        var testo = true
//        autotextViewDepart.afterTextChanged { it ->
//            if (testo && it.length == 1) {
//                GlobalScope.launch {
//                    locations = amadeus.referenceData.locations[Params
//                        .with("keyword", it)
//                        .and("subType", Locations.AIRPORT)
//                        .and("view", "LIGHT")
//                        .and("page[limit]", 10000)]
//
//                    runOnUiThread(java.lang.Runnable {
//                        val listville = mutableListOf<String>()
//
//                        runBlocking {
//                            locationDao?.deleteLocation()
//                        }
//                        var id = 1
//                        locations.map { itLoc ->
//                            val test = itLoc?.iataCode
//                            listville.add(test.toString())
//                            val loc = LocationList(id, test.toString())
//
//                            runBlocking {
//                                locationDao?.addLocation(loc)
//                            }
//                            id += 1
//                        }
//
//                        runBlocking {
//                            val loco = locationDao?.getLocation()
//                            val listString = mutableListOf<String>()
//                            loco?.map { itLou ->
//                                listString.add(itLou.name)
//                            }
//                            runOnUiThread(java.lang.Runnable {
//                                val adapter = ArrayAdapter(
//                                    this@FindFlight,
//                                    android.R.layout.simple_list_item_1, listString
//                                )
//                                autotextViewDepart.setAdapter(adapter)
//                            })
//                        }
//                    })
//                    testo = false
//                }
//            } else if (!testo && it.length < 2) {
//                runBlocking {
//                    locationDao?.deleteLocation()
//                }
//                testo = true
//            }
//
//        }


//        button_newSearch.setOnClickListener {
//            if (layout_search.visibility == View.VISIBLE)
//                layout_search.visibility = View.GONE
//            else {
//                layout_search.visibility = View.VISIBLE
//                button_newSearch.visibility = View.GONE
//            }
//        }
        layoutNoFlight.visibility = View.GONE


        btn_search.setOnClickListener {
            hideKeyboard()
            topLevel_layout.requestFocus()
            if (autoTextViewDepart.text.toString() == "" || autoTextViewRetour.text.toString() == "") {
                return@setOnClickListener
            }

            val strDepart = autoTextViewDepart.text.toString()
            val keptDepart: String = strDepart.substring(strDepart.indexOf("(") + 1)
            val lieuDepart = keptDepart.substring(0, keptDepart.indexOf(")"))
            val strRetour = autoTextViewRetour.text.toString()
            val keptRetour: String = strRetour.substring(strRetour.indexOf("(") + 1)
            val lieuRetour = keptRetour.substring(0, keptRetour.indexOf(")"))

            if (aller_date.text.toString() != "" && return_date.text.toString() != ""
                && autoTextViewDepart.text.toString() != "" && autoTextViewRetour.text.toString() != ""
            ) {
                val parsedDateDepart =
                    SimpleDateFormat("yyyy-MM-dd").parse(aller_date.text.toString())
                val parsedDateRetour =
                    SimpleDateFormat("yyyy-MM-dd").parse(return_date.text.toString())

                if (parsedDateDepart!!.before(parsedDateRetour)) {

                    dateDepart = aller_date.text.toString()
                    dateRetour = return_date.text.toString()

                    val nbAdults = spinner.selectedItem.toString().toInt()
                    flights_recyclerview.adapter = FlightsAdapter(mutableListOf())
                    layout_search.visibility = View.GONE
                    loadingPanel.visibility = View.VISIBLE
                    beginSearch(dateDepart, dateRetour, lieuDepart, lieuRetour, nbAdults)
                    activityCreate = false

                    //             button_newSearch.visibility = View.VISIBLE

                } else {
                    alert("La date de retour doit être ultérieure à celle de départ.") {
                        title = "Alert"
                        yesButton {}
                    }.show()
                    return_date.setText("")
                }
            } else if (aller_date.text.toString() != "" && return_date.visibility == View.GONE
                && autoTextViewDepart.text.toString() != "" && autoTextViewRetour.text.toString() != ""
            ) {
                dateDepart = aller_date.text.toString()
                dateRetour = ""
                val nbAdults = spinner.selectedItem.toString().toInt()
                flights_recyclerview.adapter = FlightsAdapter(mutableListOf())
                layout_search.visibility = View.GONE
                loadingPanel.visibility = View.VISIBLE
                beginSearch(dateDepart, dateRetour, lieuDepart, lieuRetour, nbAdults)
                activityCreate = false

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
            //       val flights = listFlights
            val flights = flightDao?.getFlights()
                if (!flights.isNullOrEmpty()) {
                    layoutNoFlight.visibility = View.GONE
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
                    flights_recyclerview.adapter = FlightsAdapter(flightsList)


                } else if (!activityCreate){
                    layoutNoFlight.visibility = View.VISIBLE
                }

            loadingPanel.visibility = View.GONE

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

            runOnUiThread(java.lang.Runnable {

                val test = resources.openRawResource(R.raw.optd_airline)
                val rows: List<Map<String, String>> = csvReader().readAllWithHeader(test)

                val testLogo = resources.openRawResource(R.raw.optd_airlines_websites_wkdt)
                val rowsLogo: List<Map<String, String>> = csvReader().readAllWithHeader(testLogo)

                //           Log.d("Flights", flightOffersSearches.contentToString())
                var id = 1
                var valueCa: String = ""
                var valueLogo: String = ""

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
                            val formatterHour =
                                SimpleDateFormat("HH:mm")
                            val formatterDate =
                                SimpleDateFormat("dd/MM/yyyy")
                            val dateDepartFormat =
                                formatterDate.format(parser.parse(itSegments.departure.at)!!)
                            val dateArriveeFormat =
                                formatterDate.format(parser.parse(itSegments.arrival.at)!!)
                            val heureDepartFormat =
                                formatterHour.format(parser.parse(itSegments.departure.at)!!)
                            val heureArriveeFormat =
                                formatterHour.format(parser.parse(itSegments.arrival.at)!!)



                            rows.map { itMap ->
                                itMap.map { it ->
                                    if (it.value == itSegments.carrierCode) {
                                        valueCa = itMap["name"].toString()
                                    }
                                }
                            }
                            rowsLogo.map { itMap ->
                                itMap.map { it ->
                                    if (it.value == itSegments.carrierCode) {
                                        valueLogo = itMap["2char_code"].toString()
                                    }
                                }
                            }

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
                                retour
                            )

                            runBlocking {
                                flightDao?.addFlight(flight)
                                // listFlights.add(flight)
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
        aller_date.setText(sdf.format(cal.time))
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

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}
