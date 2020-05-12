package com.example.tripin.find.hotel

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ListAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.model.Hotel
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.apptik.widget.MultiSlider
import io.apptik.widget.MultiSlider.SimpleChangeListener
import kotlinx.android.synthetic.main.activity_find_hotel.*
import kotlinx.android.synthetic.main.activity_find_hotel.btn_search
import kotlinx.android.synthetic.main.activity_find_hotel.loadingPanel
import kotlinx.android.synthetic.main.fragment_find_flight2.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*


class FindHotelActivity : AppCompatActivity() {


    private var hotelDaoSearch: HotelDao? = null
    private var hotelDaoSaved: HotelDao? = null
    private lateinit var cityCode: String
    val listCitiesFormatted = mutableListOf<List<String>>()
    val listEquipementFormatted = mutableListOf<List<String>>()
    private var cal: Calendar = Calendar.getInstance()
    var rooms_number: Int = 0
    private lateinit var dateArrivee: String
    private lateinit var dateDepart: String
    var priceMinFixed: Int = 0
    var priceMaxFixed: Int = 1000
    var priceMinChosen: Int = priceMinFixed
    var priceMaxChosen: Int = priceMaxFixed


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_hotel)
        hotels_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        loadingPanel.visibility = View.GONE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        hideKeyboard()

        val databasesearch =
            Room.databaseBuilder(this, AppDatabase::class.java, "searchDatabaseHotels").build()
        hotelDaoSearch = databasesearch.getHotelDao()
        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabaseHotels").build()
        hotelDaoSaved = databasesaved.getHotelDao()

        //création d'un client Amadeus
        val amadeus = Amadeus
            .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
            .build()


        // Gestion Date d'arrivée
        arriveeDate_date.setOnClickListener {
            hideKeyboard()
            // savedTopLevel_layout.requestFocus()
            rangeDatePickerPrimeCalendar()
        }

        // Gestion Date de départ
        departDate_date.setOnClickListener {
            hideKeyboard()
            // savedTopLevel_layout.requestFocus()
            rangeDatePickerPrimeCalendar()
        }

        //Gestion RangePrice
        price_range_slider.setMin(priceMinFixed, true, true)
        price_range_slider.setMax(priceMaxFixed, true, true)
        price_min_textview.text = priceMinFixed.toString()
        price_max_textview.text = priceMaxFixed.toString()
        min_fixed_textview.text = priceMinFixed.toString() + " €"
        max_fixed_textview.text = priceMaxFixed.toString() + " €"


        price_range_slider.setOnThumbValueChangeListener(object : SimpleChangeListener() {
            override fun onValueChanged(
                multiSlider: MultiSlider,
                thumb: MultiSlider.Thumb,
                thumbIndex: Int,
                value: Int
            ) {
                if (thumbIndex == 0) {
                    price_min_textview.text = value.toString()
                    priceMinChosen = value
                } else {
                    price_max_textview.text = value.toString()
                    priceMaxChosen = value
                }
            }
        })


        // Bouton pour lancer la recherche
        btn_search.setOnClickListener {
            hideKeyboard()
            /* if (autoTextViewDepart.text.toString() == "" || autoTextViewRetour.text.toString() == "") { // Si les lieux ne sont pas bien spécifiés
                Toast.makeText(
                    requireContext(),
                    "Veuillez choisir les destinations dans les listes déroulantes",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }*/

            runBlocking {

                val cityCsv = resources.openRawResource(R.raw.iata_airport_list)
                val listCities: List<Map<String, String>> = csvReader().readAllWithHeader(cityCsv)

                var verification: Boolean
                listCities.map { itMap ->
                    verification = false
                    var city_code = itMap["city_code"].toString()
                    var city_name = itMap["city_name"].toString().toUpperCase()
                    var city: List<String> = listOf(city_code, city_name)
                    if (listCitiesFormatted.isEmpty()) {
                        listCitiesFormatted.add(city)
                    } else {
                        listCitiesFormatted.forEach() {
                            if (it.get(1).equals(city_name)) {
                                verification = true
                            }
                        }
                        if (verification == false) {
                            listCitiesFormatted.add(city)
                        } else {
                        }
                    }
                }
            }





            btn_search.setOnClickListener {
                layoutNoHotelAvailable.visibility = View.GONE
                hideKeyboard()
                search_hotel.clearFocus()
                loadingPanel.visibility = View.VISIBLE


                val searchCity = search_hotel.text.toString()
                listCitiesFormatted.forEach() {
                    if (searchCity.equals(it.get(1), ignoreCase = true))
                        cityCode = it.get(0)

                }
                Log.d("cityCode", cityCode)
                if (search_hotel.text.toString() == "" || cityCode == null) {
                    Toast.makeText(
                        this,
                        "Veuillez entrer une destination",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                runBlocking {

                    var equipementsCsv = resources.openRawResource(R.raw.equipements)
                    var listEquipements: List<Map<String, String>> =
                        csvReader().readAllWithHeader(equipementsCsv)


                    listEquipements.map { itMap ->
                        var amenity_code = itMap["amenity_code"].toString()
                        var amenity_name = itMap["amenity_name"].toString()
                        var listEquipement: List<String> =
                            listOf(amenity_code, amenity_name)
                        listEquipementFormatted.add(listEquipement)

                    }
                }





                runBlocking {

                    Log.d("Hotel", "Début de la récupération des données")
                    hotelDaoSearch?.deleteHotels()
                    val hotels_saved_bdd = hotelDaoSaved?.getHotels()
                    rooms_number = room_number.text.toString().toInt()

                    GlobalScope.launch {
                        val hotelOffersSearches =
                            amadeus.shopping.hotelOffers[Params.with("cityCode", cityCode)
                                .and("roomQuantity", rooms_number)
                                .and(
                                    "priceRange",
                                    priceMinChosen.toString() + "-" + priceMaxChosen.toString()
                                )
                                .and("currency", "EUR")
                                .and("lang", "FR")]

                        Log.d("Hotel", "Récupération des données terminée")
                        runOnUiThread(java.lang.Runnable {
                            //permet d'appeler le block sur chacun des éléments d'une collection (== boucle for)
                            hotelOffersSearches.map { itHotelOffer ->
                                var favoris = false
                                var idHotel = itHotelOffer.hotel.hotelId

                                hotels_saved_bdd?.forEach {
                                    if (it.hotelId == idHotel) {
                                        favoris = true
                                    }
                                }

                                var description: String
                                var tel: String
                                var uri: String

                                var amenities : Array<out String>? = itHotelOffer.hotel.amenities
                                Log.d("amenities", amenities.toString())
                                var equipements = mutableListOf<String>()
                                Log.d("Equipements 1", equipements.toString())
                                equipements.clear()
                                Log.d("Equipements 2", equipements.toString())


                                if(amenities.isNullOrEmpty()){}
                                else {
                                    amenities.forEach { itAmenity ->
                                        val amenity = itAmenity
                                        listEquipementFormatted.forEach {
                                            if (amenity == it.get(0)) {
                                                equipements.add(it.get(1))
                                            } else {
                                            }

                                        }

                                    }
                                }

                                if (itHotelOffer.hotel.description == null) {
                                    description = "Description non disponible"
                                } else {
                                    description = itHotelOffer.hotel.description.text
                                }
                                if (itHotelOffer.hotel.contact == null) {
                                    tel = "Téléphone non disponible"
                                } else {
                                    tel = itHotelOffer.hotel.contact.phone
                                }
                                if (itHotelOffer.hotel.media == null) {
                                    uri = "0"
                                } else {
                                    uri = itHotelOffer.hotel.media[0].uri
                                }

                                Log.d("Equipements 3", equipements.toString())

                                val hotel = Hotel(
                                    0,
                                    itHotelOffer.hotel.hotelId,
                                    itHotelOffer.hotel.name,
                                    description,
                                    itHotelOffer.hotel.rating,
                                    uri,
                                    itHotelOffer.hotel.address.lines.joinToString(),
                                    tel,
                                    itHotelOffer.hotel.latitude,
                                    itHotelOffer.hotel.longitude,
                                    itHotelOffer.offers[0].price.total.toDouble(),
                                    equipements,
                                    favoris
                                )
                                runBlocking {
                                    hotelDaoSearch?.addHotel(hotel)
                                }

                            }

                            var hotels_search_bdd : List<Hotel> ? =null
                            runBlocking {
                                hotels_search_bdd = hotelDaoSearch?.getHotels()
                            }

                            if (!hotels_search_bdd.isNullOrEmpty()) {
                                layoutNoHotelAvailable.visibility = View.GONE
                                hotels_recyclerview.adapter =
                                    HotelsAdapter(
                                        hotels_search_bdd ?: emptyList())

                            } else {
                                layoutNoHotelAvailable.visibility = View.VISIBLE
                            }

                            loadingPanel.visibility = View.GONE
                        })
                    }
                }

            }

        }
    }


    override fun onResume() {
        super.onResume()

        val databasesearch =
            Room.databaseBuilder(this, AppDatabase::class.java, "searchDatabaseHotels").build()
        hotelDaoSearch = databasesearch.getHotelDao()
        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabaseHotels").build()
        hotelDaoSaved = databasesaved.getHotelDao()

        runBlocking {
            val hotels_search_bdd = hotelDaoSearch?.getHotels()
                hotels_recyclerview.adapter =
                    HotelsAdapter(hotels_search_bdd ?: emptyList())
            }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun rangeDatePickerPrimeCalendar() {
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
            arriveeDate_date.setText(parsedStartDate)
            departDate_date.setText(parsedEndDate)
        }

        val today = CivilCalendar()

        if (arriveeDate_date.text.toString() != "" && departDate_date.text.toString() != "") {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calStart = CivilCalendar()
            calStart.timeInMillis = df.parse(arriveeDate_date.text.toString())!!.time
            val calEnd = CivilCalendar()
            calEnd.timeInMillis = df.parse(departDate_date.text.toString())!!.time

            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .initiallyPickedRangeDays(calStart, calEnd)
                .firstDayOfWeek(Calendar.MONDAY)
                .minPossibleDate(today)
                .build()

            datePickerT.show(supportFragmentManager, "PrimeDatePickerBottomSheet")
        } else {
            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .firstDayOfWeek(Calendar.MONDAY)
                .minPossibleDate(today)
                .build()

            datePickerT.show(supportFragmentManager, "PrimeDatePickerBottomSheet")
        }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun clearFocusAutoTextView(autoCompleteTextView: AutoCompleteTextView) {
        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
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


    fun decreaseRooms(view: View) {
        if (rooms_number == 0) {
        } else {
            rooms_number = rooms_number.minus(1)
        }

        room_number.text = rooms_number.toString()
    }

    fun increaseRooms(view: View) {
        rooms_number = rooms_number.plus(1)
        room_number.text = rooms_number.toString()
    }
}





