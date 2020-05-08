package com.example.tripin.find.hotel

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.tripin.R
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
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.model.Hotel
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
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


    private var hotelDao: HotelDao? = null
    private lateinit var cityCode: String
    val list_hotels: MutableList<Hotel> = mutableListOf()
    val listCitiesFormatted = mutableListOf<List<String>>()
    val listEquipementFormatted = mutableListOf<List<String>>()
    private var cal: Calendar = Calendar.getInstance()
    var rooms_number: Int = 0
    private lateinit var dateArrivee: String
    private lateinit var dateDepart: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_hotel)
        hotels_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        loadingPanel.visibility = View.GONE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        hideKeyboard()

        //création d'un client Amadeus
        val amadeus = Amadeus
            .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
            .build()

        //TODO : mettre des dates d'arrivée et de départ
        arriveeDate_date.setOnClickListener {
            hideKeyboard()
           // savedTopLevel_layout.requestFocus()
            rangeDatePickerPrimeCalendar()
        }

        // Affiche le calendrier pour choisir la date de retour
        departDate_date.setOnClickListener {
            hideKeyboard()
           // savedTopLevel_layout.requestFocus()
            rangeDatePickerPrimeCalendar()
        }

        // Bouton pour lancer la recherche
        btn_search.setOnClickListener {
            hideKeyboard()
            savedTopLevel_layout.requestFocus()
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

            val database =
                Room.databaseBuilder(this, AppDatabase::class.java, "allhotels").build()
            hotelDao = database.getHotelDao()



            btn_search.setOnClickListener {
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
                        var amenity_icon = itMap["amenity_icon"].toString()
                        var listEquipement: List<String> =
                            listOf(amenity_code, amenity_name, amenity_icon)
                        listEquipementFormatted.add(listEquipement)

                    }
                }





                runBlocking {

                    Log.d("Hotel", "Début de la récupération des données")
                    list_hotels.clear()
                    val bdd_hotels = hotelDao?.getHotels()

                    GlobalScope.launch {
                        val hotelOffersSearches =
                            amadeus.shopping.hotelOffers[Params.with("cityCode", cityCode)]
                        Log.d("Hotel", "Récupération des données terminée")
                        runOnUiThread(java.lang.Runnable {
                            //permet d'appeler le block sur chacun des éléments d'une collection (== boucle for)
                            hotelOffersSearches.map { itHotelOffer ->
                                var favoris = false
                                var idHotel = itHotelOffer.hotel.hotelId

                                bdd_hotels?.forEach {
                                    if (it.hotelId == idHotel) {
                                        favoris = true
                                    }
                                }

                                var description: String
                                var email: String
                                var tel: String
                                var uri: String

                                var amenities = itHotelOffer.hotel.amenities
                                var equipements = mutableListOf<String>()
                                equipements.clear()

                                amenities.forEach { itAmenity ->
                                    val amenity = itAmenity
                                    listEquipementFormatted.forEach {
                                        if (amenity == it.get(0)) {
                                            equipements.add(it.get(1))
                                        } else {
                                        }

                                    }

                                }

                                if (itHotelOffer.hotel.description == null) {
                                    description = "Description non disponible"
                                } else {
                                    description = itHotelOffer.hotel.description.text
                                }
                                if (itHotelOffer.hotel.contact == null) {
                                    email = "Email non disponible"
                                    tel = "Téléphone non disponible"
                                } else {
                                    //TODO : Résoudre problème récupération email
                                    email = "email@test.fr"
                                    tel = itHotelOffer.hotel.contact.phone
                                }
                                if (itHotelOffer.hotel.media == null) {
                                    uri = "0"
                                } else {
                                    uri = itHotelOffer.hotel.media[0].uri
                                }


                                val hotel = Hotel(
                                    0,
                                    itHotelOffer.hotel.hotelId,
                                    itHotelOffer.hotel.name,
                                    description,
                                    itHotelOffer.hotel.rating,
                                    uri,
                                    itHotelOffer.hotel.address.lines.joinToString(),
                                    email,
                                    tel,
                                    equipements,
                                    favoris
                                )
                                runBlocking {
                                    list_hotels.add(hotel)
                                }
                            }


                            hotels_recyclerview.adapter =
                                HotelsAdapter(
                                    list_hotels ?: emptyList()
                                )
                            loadingPanel.visibility = View.GONE
                        })
                    }
                }

            }

        }
    }


    override fun onResume() {
        super.onResume()
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





