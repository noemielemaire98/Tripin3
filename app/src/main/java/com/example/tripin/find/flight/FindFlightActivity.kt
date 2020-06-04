package com.example.tripin.find.flight

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ListAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.resources.FlightOfferSearch
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.aminography.primedatepicker.picker.theme.LightThemeFactory
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.model.Flight
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.android.synthetic.main.activity_find_flight.*
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
    private lateinit var travelClass: String
    private var flightDao: FlightDao? = null
    private lateinit var flightsRecyclerView: RecyclerView

    //    private var listFlights = mutableListOf<Flight>()
    private var activityCreate = true
    private val amadeus: Amadeus = Amadeus
        .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_flight)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        flightsRecyclerView =
            findViewById<View>(R.id.flights_recyclerview) as RecyclerView
        flightsRecyclerView.layoutManager = LinearLayoutManager(this)

        val themeFactory = object : LightThemeFactory() {
//                    override val calendarViewFlingOrientation: PrimeCalendarView.FlingOrientation
//                        get() = PrimeCalendarView.FlingOrientation.HORIZONTAL

            // Other customizations...
            override val selectionBarBackgroundColor: Int
                get() = super.getColor(R.color.contrast_blue)

            val calendarViewPickedDayCircleColor: Int
                get() = super.getColor(R.color.contrast_blue)
        }

        // Affiche le calendrier pour choisir la date d'aller
        aller_date.setOnClickListener {
            hideKeyboard()
            savedTopLevel_layout.requestFocus()
            if (return_dateLayout.visibility == View.VISIBLE) { // Si c'est un voyage aller-retour

                rangeDatePickerPrimeCalendar(themeFactory)

            } else if (return_dateLayout.visibility == View.GONE) { // Si c'est un voyage avec aller simple

                val singleDayPickCallback = SingleDayPickCallback { date ->
                    // TODO
                    Log.d("Date", date.shortDateString)
                    val parser =
                        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    val formatterDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val parsedDate =
                        formatterDate.format(parser.parse(date.shortDateString)!!)
                    aller_date.setText(parsedDate)
                }

                val today =
                    CivilCalendar()

                if (aller_date.text.toString() != "") { // Si une date a déjà été choisie
                    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val calStart = CivilCalendar()
                    calStart.timeInMillis = df.parse(aller_date.text.toString())!!.time

                    val datePickerT = PrimeDatePicker.dialogWith(today)
                        .pickSingleDay(singleDayPickCallback)
                        .initiallyPickedSingleDay(calStart)
                        .firstDayOfWeek(Calendar.MONDAY)
                        .applyTheme(themeFactory)
                        .minPossibleDate(today)
                        .build()

                    datePickerT.show(supportFragmentManager, "PrimeDatePickerBottomSheet")
                } else { // Si aucune date n'a encore été choisie

                    val datePickerT = PrimeDatePicker.dialogWith(today)
                        .pickSingleDay(singleDayPickCallback)
                        .firstDayOfWeek(Calendar.MONDAY)
                        .applyTheme(themeFactory)
                        .minPossibleDate(today)
                        .build()

                    datePickerT.show(supportFragmentManager, "PrimeDatePickerBottomSheet")
                }
            }
        }

        // Affiche le calendrier pour choisir la date de retour
        return_date.setOnClickListener {
            hideKeyboard()
            savedTopLevel_layout.requestFocus()
            rangeDatePickerPrimeCalendar(themeFactory)
        }

        // Initialise la liste déroulante du nombre de passagers
        val passengersNumber = resources.getStringArray(R.array.passengersNumber)
        val adapterPassengers = IgnoreAccentsArrayAdapter(
            this@FindFlightActivity,
            android.R.layout.simple_list_item_1, passengersNumber
        )
        passengers_number.setAdapter(adapterPassengers)

        // Montre toute la liste déroulante à chaque fois
        passengers_number.setOnClickListener {
            hideKeyboard()
            savedTopLevel_layout.requestFocus()
            passengers_number.showDropDown()
        }

        // Initialise la liste déroulante des classes de vol
        val travelClassList = resources.getStringArray(R.array.travelClass)
        val adapterTravelClass = IgnoreAccentsArrayAdapter(
            this@FindFlightActivity,
            android.R.layout.simple_list_item_1, travelClassList
        )
        travelClassEdit.setAdapter(adapterTravelClass)

        // Montre toute la liste déroulante à chaque fois
        travelClassEdit.setOnClickListener {
            hideKeyboard()
            savedTopLevel_layout.requestFocus()
            travelClassEdit.showDropDown()
        }


        runBlocking {
            // Récupère la liste des aéroports dans le csv correspondant
            val airportCsv = resources.openRawResource(R.raw.iata_airport_list)
            val listAirports: List<Map<String, String>> = csvReader().readAllWithHeader(airportCsv)

            val listAirportsFormatted = mutableListOf<String>()

            // Affiche uniquement les infos utiles des aéroports dans une liste
            listAirports.map { itMap ->
                listAirportsFormatted.add(
                    itMap["city_name"].toString()
                        .toUpperCase(Locale.ROOT) + " " + itMap["por_name"].toString()
                        .toUpperCase(Locale.ROOT) + " (" + itMap["por_code"].toString().toUpperCase(
                        Locale.ROOT
                    ) + ")"
                )
            }

            // Initialise la liste déroulante du lieu de départ
            val adapterLieuDepart = IgnoreAccentsArrayAdapter(
                this@FindFlightActivity,
                android.R.layout.simple_list_item_1, listAirportsFormatted.toTypedArray()
            )
            // Initialise la liste déroulante du lieu d'arrivée
            val adapterLieuArrival = IgnoreAccentsArrayAdapter(
                this@FindFlightActivity,
                android.R.layout.simple_list_item_1, listAirportsFormatted.toTypedArray()
            )
            autoTextViewDepart.setAdapter(adapterLieuDepart)
            autoTextViewRetour.setAdapter(adapterLieuArrival)
        }
        //empty autoTextView if not chosen from list
        clearFocusAutoTextView(autoTextViewDepart)
        clearFocusAutoTextView(autoTextViewRetour)

        // Bouton pour lancer la recherche
        btn_search.setOnClickListener {
            hideKeyboard()
            savedTopLevel_layout.requestFocus()
            if (autoTextViewDepart.text.toString() == "" || autoTextViewRetour.text.toString() == "") { // Si les lieux ne sont pas bien spécifiés
                Toast.makeText(
                    this,
                    "Veuillez choisir les destinations dans les listes déroulantes",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Récupère les code IATA des lieux
            val strDepart = autoTextViewDepart.text.toString()
            val keptDepart = strDepart.substringAfterLast("(")
            lieuDepart = keptDepart.substringBeforeLast(")")
            val strRetour = autoTextViewRetour.text.toString()
            val keptRetour = strRetour.substringAfterLast("(")
            lieuRetour = keptRetour.substringBeforeLast(")")

            if (aller_date.text.toString() != "" && return_date.text.toString() != "") { // Si lieux bien spécifiés et voyage aller-retour

                dateRetour = return_date.text.toString()
                beginSearchExported(dateRetour)

            } else if (aller_date.text.toString() != "" && return_dateLayout.visibility == View.GONE) { // Si voyage aller-simple
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

        // Choix du type de voyage (aller simple ou aller-retour)
        allerType_radiogroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            if (radio.id == simple_button.id) {
                return_dateLayout.visibility = View.GONE
                return_date.setText("")

            } else {
                return_dateLayout.visibility = View.VISIBLE
            }
        }

        // Initialise la BDD
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
            if (!flights.isNullOrEmpty()) { // S'il y a des vols de trouvés
                layoutNoFlightAvailable.visibility = View.GONE
                //            val flights = listFlights
                val flightsList: MutableList<MutableList<Flight>> = mutableListOf()
                var testlist: MutableList<Flight> = mutableListOf()
                var travelId = 1
                var position = 0
                // map pour regrouper les vols d'un même itinéraire
                flights.map {
                    val flight = flights[it.id - 1]
                    if (it.travelId != travelId) { // Si appartient au même itinéraire
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
                flightsRecyclerView.adapter = FlightsAdapter(flightsList, findViewById(android.R.id.content))
            } else if (!activityCreate) { // S'il n'y a pas de vols et qu'une recherche a été effectuée, affiche l'image aucun vol dispo
                layoutNoFlightAvailable.visibility = View.VISIBLE
            }
            loadingPanel.visibility = View.GONE // cache roue de chargement
        }
    }

    // Lancement de la recherche
    private fun beginSearchExported(dateRetour: String) {
        dateDepart = aller_date.text.toString()

        travelClass =
            if (travelClassEdit.text.toString() != "PREMIUM ECO") travelClassEdit.text.toString() else "PREMIUM_ECONOMY"
        val nbAdults = passengers_number.text.toString().toInt()
        flights_recyclerview.adapter = FlightsAdapter(mutableListOf(), findViewById(android.R.id.content))
        layout_search.visibility = View.GONE // cache le formulaire
        loadingPanel.visibility = View.VISIBLE // affiche la roue de chargement
        layoutNoFlightAvailable.visibility = View.GONE // cache l'image aucun vol dispo
        runBlocking {
            beginSearch(dateDepart, dateRetour, lieuDepart, lieuRetour, travelClass, nbAdults)
        }
        activityCreate = false
    }

    // Appel API et enregistrement BDD
    private suspend fun beginSearch(
        dateDepart: String,
        dateRetour: String,
        lieuDepart: String,
        lieuRetour: String,
        travelClass: String,
        nbAdults: Int
    ) {
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        flightDao?.deleteFlights()

        scope.launch {

            val flightOffersSearches: Array<FlightOfferSearch>
            if (dateRetour != "") { // si aller-retour
                flightOffersSearches =
                    amadeus.shopping.flightOffersSearch[Params
                        .with("originLocationCode", lieuDepart)
                        .and("destinationLocationCode", lieuRetour)
                        .and("departureDate", dateDepart)
                        .and("returnDate", dateRetour)
                        .and("adults", nbAdults)
                        .and("max", 10)
                        .and("travelClass", travelClass)]
            } else { // si aller simple
                flightOffersSearches =
                    amadeus.shopping.flightOffersSearch[Params
                        .with("originLocationCode", lieuDepart)
                        .and("destinationLocationCode", lieuRetour)
                        .and("departureDate", dateDepart)
                        .and("adults", nbAdults)
                        .and("max", 10)
                        .and("travelClass", travelClass)]
            }

            withContext(Dispatchers.Main) {

                // recupère les noms des compagnies aériennes
                val airlinesNamesCsv = resources.openRawResource(R.raw.optd_airline)
                val rows: List<Map<String, String>> = csvReader().readAllWithHeader(airlinesNamesCsv)

//                val testLogo = resources.openRawResource(R.raw.optd_airlines_websites_wkdt)
//                val rowsLogo: List<Map<String, String>> = csvReader().readAllWithHeader(testLogo)

                var id = 1 // pour incrémenter les id
                var carrierName = ""
                var carrierCodeLogo = ""
                val flightsList: MutableList<MutableList<Flight>> = mutableListOf()

                flightOffersSearches.map { itFlight ->
                    var retour = 0 // pour savoir si voyage aller ou voyage retour
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
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            val formatterHour =
                                SimpleDateFormat("HH:mm", Locale.getDefault())
                            val formatterDate =
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val dateDepartFormat =
                                formatterDate.format(parser.parse(departureDate)!!)
                            val dateArrivalFormat =
                                formatterDate.format(parser.parse(arrivalDate)!!)
                            val heureDepartFormat =
                                formatterHour.format(parser.parse(departureDate)!!)
                            val heureArrivalFormat =
                                formatterHour.format(parser.parse(arrivalDate)!!)


                            rows.map { itMap ->
                                itMap.map {
                                    if (it.value == itSegments.carrierCode) {
                                        carrierName = itMap["name"].toString()
                                        carrierCodeLogo = itMap["2char_code"].toString()
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
                                dateArrivalFormat,
                                heureArrivalFormat,
                                dureeFormat,
                                itSegments.departure.iataCode,
                                itSegments.arrival.iataCode,
                                itSegments.carrierCode,
                                carrierCodeLogo,
                                carrierName,
                                nbEscales,
                                retour,
                                false,
                                uuid
                            )
                            uuidSomme += uuid // Somme des uuid d'un même itinéraire
                            listFlightAdd.add(flight)

                            id += 1
                        }


                        retour += 1
                    }
                    // modifie l'uuid pour mettre celui commun à l'itinéraire
                    listFlightAdd.map { itListF ->
                        itListF.uuid = uuidSomme
                        flightDao?.addFlight(itListF)
                    }
                    flightsList.add(listFlightAdd)

                }
                // Si au moins un vol de trouvé
                if (!flightsList.isNullOrEmpty()) {
                    layoutNoFlightAvailable.visibility = View.GONE
                    flightsRecyclerView.adapter = FlightsAdapter(flightsList, findViewById(android.R.id.content))
                } else if (!activityCreate) {
                    layoutNoFlightAvailable.visibility = View.VISIBLE
                }

                loadingPanel.visibility = View.GONE // cache roue de chargement
            }
        }

    }

    // vide textView si non choisi dans la liste
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

    // affichage du calendrier aller-retour
    private fun rangeDatePickerPrimeCalendar(themeFactory: LightThemeFactory) {
        val rangeDaysPickCallback = RangeDaysPickCallback { startDate, endDate ->
            // TODO
            Log.d("Date", "${startDate.shortDateString} ${endDate.shortDateString}")
            val parser =
                SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val formatterDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedStartDate =
                formatterDate.format(parser.parse(startDate.shortDateString)!!)
            val parsedEndDate =
                formatterDate.format(parser.parse(endDate.shortDateString)!!)
            aller_date.setText(parsedStartDate)
            return_date.setText(parsedEndDate)
        }

        val today =
            CivilCalendar()

        if (aller_date.text.toString() != "" && return_date.text.toString() != "") {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calStart = CivilCalendar()
            calStart.timeInMillis = df.parse(aller_date.text.toString())!!.time
            val calEnd =
                CivilCalendar()
            calEnd.timeInMillis = df.parse(return_date.text.toString())!!.time

            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .initiallyPickedRangeDays(calStart, calEnd)
                .firstDayOfWeek(Calendar.MONDAY)
                .applyTheme(themeFactory)
                .minPossibleDate(today)
                .build()

            datePickerT.show(supportFragmentManager, "PrimeDatePickerBottomSheet")
        } else {
            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .firstDayOfWeek(Calendar.MONDAY)
                .minPossibleDate(today)
                .applyTheme(themeFactory)
                .build()

            datePickerT.show(supportFragmentManager, "PrimeDatePickerBottomSheet")
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
