package com.example.tripin.find.hotel

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback

import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.CityDao
import com.example.tripin.data.HotelDao
import com.example.tripin.model.Hotel
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.apptik.widget.MultiSlider
import kotlinx.android.synthetic.main.activity_find_hotel.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.support.v4.runOnUiThread
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FindHotelFragment : Fragment() {

    private var hotelDaoSearch: HotelDao? = null
    private var hotelDaoSaved: HotelDao? = null
    private var citydao: CityDao? = null
    private lateinit var cityCode: String
    val listCitiesFormatted = mutableListOf<List<String>>()
    val listEquipementFormatted = mutableListOf<List<String>>()
    private var cal: Calendar = Calendar.getInstance()
    var rooms_number: Int = 1
    private lateinit var dateArrivee: String
    private lateinit var dateDepart: String
    var priceMinFixed: Int = 0
    var priceMaxFixed: Int = 1000
    var priceMinChosen: Int = priceMinFixed
    var priceMaxChosen: Int = priceMaxFixed
    var list_favoris  = arrayListOf<Boolean>()
    var list_cities_name = arrayListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_find_hotel2, container, false)
        val recyclerViewHotels = view.findViewById<RecyclerView>(R.id.hotels_recyclerview)
        val bt_search = view.findViewById<Button>(R.id.btn_search)
        val editText = view.findViewById<AutoCompleteTextView>(R.id.search_hotel)
        val roomNumber = view.findViewById<TextView>(R.id.room_number)
        val arriveeDate = view.findViewById<EditText>(R.id.arriveeDate_date)
        val departDate = view.findViewById<EditText>(R.id.departDate_date)
        val priceRange = view.findViewById<MultiSlider>(R.id.price_range_slider)
        val priceMin = view.findViewById<TextView>(R.id.price_min_textview)
        val priceMax = view.findViewById<TextView>(R.id.price_max_textview)
        val priceMinFxd = view.findViewById<TextView>(R.id.min_fixed_textview)
        val priceMaxFxd = view.findViewById<TextView>(R.id.max_fixed_textview)
        val loadingPanel = view.findViewById<RelativeLayout>(R.id.loadingPanel)
        val decreaseButton = view.findViewById<ImageButton>(R.id.decrease_rooms)
        val increaseButton = view.findViewById<ImageButton>(R.id.increase_rooms)
        //Initialisation Recyclerview
        recyclerViewHotels.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        loadingPanel.visibility = View.GONE

        //Initialisation Database
        val databasesearch =
            Room.databaseBuilder(
                requireActivity().baseContext,
                AppDatabase::class.java,
                "searchDatabase"
            ).build()
        val databasesaved =
            Room.databaseBuilder(
                requireActivity().baseContext,
                AppDatabase::class.java,
                "savedDatabase"
            ).build()


        hotelDaoSaved = databasesaved.getHotelDao()
        citydao = databasesaved.getCityDao()
        hotelDaoSearch = databasesearch.getHotelDao()



        //création d'un client Amadeus
        val amadeus = Amadeus
            .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
            .build()

        runBlocking {
            val list_cities_bdd = citydao?.getCity()
            list_cities_bdd?.map {
                if(it.iataCode != null){
                list_cities_name.add(it.name!!)
            }
            }
        }
        val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,list_cities_name)
        editText.setAdapter(adapter)

        //Gestion nombre de chambres
        roomNumber.text = rooms_number.toString()


        // Gestion Date d'arrivée
        arriveeDate.setOnClickListener {
            hideKeyboard()
            rangeDatePickerPrimeCalendar()

        }


        // Gestion Date de départ
        departDate.setOnClickListener {
            hideKeyboard()
            rangeDatePickerPrimeCalendar()
        }

        decreaseButton.setOnClickListener {
            hideKeyboard()
            if (rooms_number == 1) {
            } else {
                rooms_number = rooms_number.minus(1)
            }

            roomNumber.text = rooms_number.toString()
        }

        increaseButton.setOnClickListener {
            hideKeyboard()
            if (rooms_number == 9) {
            } else {
                rooms_number = rooms_number.plus(1)
            }
            roomNumber.text = rooms_number.toString()
        }

        //Gestion Gamme de Prix
        priceRange.setMin(priceMinFixed, true, true)
        priceRange.setMax(priceMaxFixed, true, true)
        priceMin.text = priceMinFixed.toString()
        priceMax.text = priceMaxFixed.toString()
        priceMinFxd.text = priceMinFixed.toString() + " €"
        priceMaxFxd.text = priceMaxFixed.toString() + " €"


        priceRange.setOnThumbValueChangeListener(object :
            MultiSlider.SimpleChangeListener() {
            override fun onValueChanged(
                multiSlider: MultiSlider,
                thumb: MultiSlider.Thumb,
                thumbIndex: Int,
                value: Int
            ) {
                hideKeyboard()
                if (thumbIndex == 0) {
                    priceMin.text = value.toString()
                    priceMinChosen = value
                } else {
                    priceMax.text = value.toString()
                    priceMaxChosen = value
                }
            }
        })


        //Lancer la recherche
        bt_search.setOnClickListener {
            hideKeyboard()
            layoutNoHotelAvailable.visibility = View.GONE
            editText.clearFocus()
            loadingPanel.visibility = View.VISIBLE

            //Récupération de la liste des cityCode
            runBlocking {
                val searchCity = editText.text.toString()
                val list_cities_bdd = citydao?.getCity()
                list_cities_bdd?.map {
                    if (it.name == searchCity){
                        cityCode = it.iataCode
                    }
                }
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

            dateArrivee = arriveeDate.text.toString()
            dateDepart = departDate.text.toString()

            runBlocking {

                Log.d("Hotel", "Début de la récupération des données")
                hotelDaoSearch?.deleteHotels()
                val hotels_saved_bdd = hotelDaoSaved?.getHotels()
                list_favoris.clear()
                rooms_number = room_number.text.toString().toInt()

                //Récupération des données
                GlobalScope.launch {
                    Log.d("price",  priceMinChosen.toString() + "-" + priceMaxChosen.toString())
                    val hotelOffersSearches =
                        amadeus.shopping.hotelOffers[Params.with("cityCode", cityCode)
                            .and("roomQuantity", rooms_number)
                            .and("priceRange", priceMinChosen.toString() + "-" + priceMaxChosen.toString())
                            .and("checkInDate", dateArrivee)
                            .and("checkOutDate", dateDepart)
                            .and("currency", "EUR")
                            .and("lang", "FR")]

                    Log.d("Hotel", "Récupération des données terminée")


                    runOnUiThread {
                        hotelOffersSearches.map { itHotelOffer ->
                            Log.d("Hotel", itHotelOffer.toString())
                            var idHotel = itHotelOffer.hotel.hotelId

                            var name = itHotelOffer.hotel.name

                            var description: String
                            if (itHotelOffer.hotel.description == null) {
                                description = "Description non disponible"
                            } else {
                                description = itHotelOffer.hotel.description.text
                            }

                            var rate = itHotelOffer.hotel.rating

                            var uri: String
                            if (itHotelOffer.hotel.media == null) {
                                uri = "0"
                            } else {
                                uri = itHotelOffer.hotel.media[0].uri
                            }


                            var adresse: MutableList<String> = mutableListOf()
                            val geocoder = Geocoder(activity)
                            val adresseList = geocoder.getFromLocation(
                                itHotelOffer.hotel.latitude,
                                itHotelOffer.hotel.longitude,
                                1
                            )
                            adresseList.map {
                                adresse.add(it.featureName)
                                adresse.add(it.thoroughfare)
                                adresse.add(it.postalCode)
                                adresse.add(it.locality)
                                adresse.add(it.countryName)
                            }


                            var telephone: String
                            if (itHotelOffer.hotel.contact == null) {
                                telephone = "Téléphone non disponible"
                            } else {
                                telephone = itHotelOffer.hotel.contact.phone
                            }


                            var latitude = itHotelOffer.hotel.latitude
                            var longitude = itHotelOffer.hotel.longitude


                            var price: Double = -1.0
                            itHotelOffer.offers.map {
                                if (price < 0) {
                                    price = it.price.total.toDouble()
                                } else if (price < it.price.total.toDouble()) {
                                } else if (price > it.price.total.toDouble()) {
                                    price = it.price.total.toDouble()
                                }
                            }


                            //TODO : régler problème des équipements
                            var equipements = mutableListOf<String>()
                            if (itHotelOffer.hotel.amenities.isNullOrEmpty()) {
                            } else {
                                itHotelOffer.hotel.amenities.forEach { itAmenity ->
                                    val amenity = itAmenity
                                    listEquipementFormatted.forEach {
                                        if (amenity == it[0]) {
                                            val nom = it[1]
                                            var test = true
                                            equipements.forEach {
                                                if(it == amenity){
                                                    test = false }
                                            }
                                            if(test){
                                                equipements.add(it[1])
                                            }
                                        } else {
                                        }
                                    }
                                }
                            }


                            var listOfferId: MutableList<String> = mutableListOf()
                            itHotelOffer.offers.map {
                                var offerId = it.id
                                listOfferId.add(offerId)
                            }


                            var match_bdd = false
                            var favori = false
                            hotels_saved_bdd?.forEach {
                                if (it.hotelId == idHotel) {
                                    list_favoris.add(true)
                                    match_bdd = true
                                    favori = true
                                }
                            }
                            if (match_bdd == false ){list_favoris.add(false)}

                            //Création de l'hôtel
                            val hotel = Hotel(
                                0,
                                idHotel,
                                name,
                                description,
                                rate,
                                uri,
                                adresse,
                                telephone,
                                latitude,
                                longitude,
                                price,
                                equipements,
                                listOfferId,
                                favori
                            )




                            runBlocking {
                                hotelDaoSearch?.addHotel(hotel)
                            }
                        }

                        var hotels_search_bdd: List<Hotel>? = null

                        runBlocking {
                            hotels_search_bdd = hotelDaoSearch?.getHotels()
                        }

                        if (!hotels_search_bdd.isNullOrEmpty()) {
                            layoutNoHotelAvailable.visibility = View.GONE
                            recyclerViewHotels.adapter =
                                HotelsAdapter(hotels_search_bdd ?: emptyList(), list_favoris)
                        } else {
                            layoutNoHotelAvailable.visibility = View.VISIBLE
                        }
                        loadingPanel.visibility = View.GONE
                    }
                }

            }
        }

        return view
    }


    override fun onResume() {
        super.onResume()

        list_favoris.clear()
        val databasesearch =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "searchDatabase")
                .build()
        val databasesaved =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "savedDatabase")
                .build()
        hotelDaoSaved = databasesaved.getHotelDao()
        hotelDaoSearch = databasesearch.getHotelDao()

        runBlocking {
            val hotels = hotelDaoSearch?.getHotels()
            val list_hotels_bdd = hotelDaoSaved?.getHotels()

            hotels?.map {
                val id = it.id
                var match_bdd = false
                list_hotels_bdd?.forEach {
                    if (it.id == id) {
                        list_favoris.add(true)
                        match_bdd = true
                    }
                }
                if (match_bdd == false) {
                    list_favoris.add(false)
                }
                hotels_recyclerview.adapter =
                    HotelsAdapter(hotels ?: emptyList(), list_favoris)
            }


        }

    }


    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun rangeDatePickerPrimeCalendar() {
        val rangeDaysPickCallback = RangeDaysPickCallback { startDate, endDate ->
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

            datePickerT.show(childFragmentManager, "PrimeDatePickerBottomSheet")
        } else {
            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .firstDayOfWeek(Calendar.MONDAY)
                .minPossibleDate(today)
                .build()

            datePickerT.show(childFragmentManager, "PrimeDatePickerBottomSheet")
        }
    }

}
