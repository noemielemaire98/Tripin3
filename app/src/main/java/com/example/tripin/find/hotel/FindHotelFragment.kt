package com.example.tripin.find.hotel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.provider.FontsContractCompat.FontRequestCallback.RESULT_OK
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback

import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.CityDao
import com.example.tripin.data.HotelDao
import com.example.tripin.data.retrofitHotel
import com.example.tripin.model.Hotel
import io.apptik.widget.MultiSlider
import kotlinx.android.synthetic.main.activity_find_hotel.*
import kotlinx.android.synthetic.main.activity_gestion_rooms.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FindHotelFragment : Fragment() {

    private var hotelDaoSearch: HotelDao? = null
    private var hotelDaoSaved: HotelDao? = null
    private var citydao: CityDao? = null
    private var cityCode: Int= 0
    val listCitiesFormatted = mutableListOf<List<String>>()
    val listEquipementFormatted = mutableListOf<List<String>>()
    private var cal: Calendar = Calendar.getInstance()
    var rooms_number: Int = 1
    private lateinit var dateArrivee: String
    private lateinit var dateDepart: String
    var priceMinFixed: Int = 0
    var priceMaxFixed: Int = 5000
    var priceMinChosen: Int = priceMinFixed
    var priceMaxChosen: Int = priceMaxFixed
    var list_favoris  = arrayListOf<Boolean>()
    var list_cities_name = arrayListOf<String>()
    private val service = retrofitHotel().create(HotelAPI::class.java)
    private val hotelKey = "e510fb173emsh2748fdaccbfd76dp19ee52jsnc2bda03d8b6d"
    private var sortBy : String = ""
    var adultsList : ArrayList<String> ?= arrayListOf()



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
        val increaseButton = view.findViewById<ImageButton>(R.id.increase_rooms)
        val hotelsLayout = view.findViewById<RelativeLayout>(R.id.hotelsFind_layout)
        val bt_best_seller = view.findViewById<Button>(R.id.best_seller)
        val bt_highest_first = view.findViewById<Button>(R.id.highest_first)
        val bt_lowest_first = view.findViewById<Button>(R.id.lowest_first)
        val bt_price_sort = view.findViewById<Button>(R.id.price)
        val bt_highest_price = view.findViewById<Button>(R.id.highest_price)


        //Initialisation Recyclerview
        recyclerViewHotels.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)



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


        //Initialisation du nombre de filtres de recherche
        val listButtons = listOf<Button>(bt_best_seller,bt_highest_first,bt_lowest_first,bt_price_sort,bt_highest_price)
        listener_bouton(bt_best_seller,listButtons,requireContext())
        listener_bouton(bt_highest_first,listButtons,requireContext())
        listener_bouton(bt_lowest_first,listButtons,requireContext())
        listener_bouton(bt_price_sort,listButtons,requireContext())
        listener_bouton(bt_highest_price,listButtons,requireContext())

        //Affichage des villes
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

        //Gestion du nombre de chambres
        adultsList?.clear()
        roomNumber.text = adultsList?.size.toString()
        increaseButton.setOnClickListener {
            val intent= Intent(it.context, RoomsGestion::class.java)
            startActivityForResult(intent, 1)
        }

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


        // Gestion du prix
        priceRange.setMin(priceMinFixed, true, false)
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
            loadingPanel.visibility = View.VISIBLE
            hideKeyboard()
            editText.clearFocus()

            //Récupération des dates

                dateArrivee = arriveeDate.text.toString()
                dateDepart = departDate.text.toString()

                //Récupération du filtre de recherche
                sortBy =
                    liste_cat_active(bt_best_seller, bt_highest_first, bt_lowest_first, bt_price_sort, bt_highest_price)

            //Recuperation du code de la ville de l'API
           runBlocking {
                hotelDaoSearch?.deleteHotels()
                val searchCity = editText.text.toString()
                var result = service.getLocation("fr_FR",searchCity.toLowerCase(),hotelKey)

                result.suggestions.map {
                    if (it.group == "CITY_GROUP"){
                        cityCode = it.entities[0].destinationId.toInt()
                    }
                }
            }

            //Lancement de la recherche
            runBlocking {
                hotelDaoSearch?.deleteHotels()
                val hotels_saved_bdd = hotelDaoSaved?.getHotels()
                list_favoris.clear()


                Log.d("Hotel", "Début de la récupération des données")
                var result: ModelRapid.Hotels? = null
                when (adultsList?.size) {
                    1 -> {
                        result = service.getHotelsList(cityCode,1, dateArrivee, dateDepart,10,
                            adultsList!!.get(0).toInt(), null,null, null,
                            sortBy,priceMinChosen, priceMaxChosen,"fr_FR","EUR",hotelKey )
                    }
                    2 -> {
                        result = service.getHotelsList(cityCode,1, dateArrivee, dateDepart,10,
                            adultsList!!.get(0).toInt(),  adultsList!!.get(1).toInt(),null, null,
                            sortBy,priceMinChosen, priceMaxChosen,"fr_FR","EUR",hotelKey )
                    }
                    3 -> {
                        result = service.getHotelsList(cityCode,1, dateArrivee, dateDepart,10,
                            adultsList!!.get(0).toInt(),  adultsList!!.get(1).toInt(),adultsList!!.get(2).toInt(), null,
                            sortBy,priceMinChosen, priceMaxChosen,"fr_FR","EUR",hotelKey )
                    }
                    4 -> {
                        result = service.getHotelsList(cityCode,1, dateArrivee, dateDepart,10,
                            adultsList!!.get(0).toInt(), adultsList!!.get(1).toInt(),adultsList!!.get(2).toInt(), adultsList!!.get(3).toInt(),
                            sortBy,priceMinChosen, priceMaxChosen,"fr_FR","EUR",hotelKey)
                    }
                    else -> {
                        Toast.makeText(requireActivity().baseContext, "Veuillez ajouter des chambres", Toast.LENGTH_SHORT).show()
                    }
                }

                Log.d("Hotel", "Récupération des données terminée")



                if(result!=null) {
                    Log.d("IMAGESPB", "tout : ${result}")

                    result.data.body.searchResults.results.map {
                        var idHotel = it.id.toString()
                        var adresse: MutableList<String> = mutableListOf()
                        val geocoder = Geocoder(activity)
                        val adresseList = geocoder.getFromLocation(
                            it.coordinate.lat,
                            it.coordinate.lon,
                            1
                        )
                        Log.d("IMAGES", "test : ${it}")


                        //Gestion de l'adresse
                        adresseList.map {
                            adresse.add(it.featureName)
                            if (it.thoroughfare == null) {
                                adresse.add("null")
                            } else {
                                adresse.add(it.thoroughfare)
                            }
                            adresse.add(it.postalCode)
                            adresse.add(it.locality)
                            adresse.add(it.countryName)
                        }


                        //Récupération des hôtels favoris
                        var match_bdd = false
                        var favori = false
                        hotels_saved_bdd?.forEach {
                            if (it.hotelId == idHotel.toInt()) {
                                list_favoris.add(true)
                                match_bdd = true
                                favori = true
                            }
                        }
                        if (!match_bdd) {
                            list_favoris.add(false)
                        }


                        val hotel = Hotel(
                            0,
                            it.id.toInt(),
                            it.name,
                            null,
                            it.starRating.toInt(), // TODO : PB with long ? NewYork
                            it.thumbnailUrl,
                            adresse,
                            null,
                            it.coordinate.lat,
                            it.coordinate.lon,
                            it.ratePlan.price.current,
                            null

                        )

                            hotelDaoSearch?.addHotel(hotel)
                            Log.d("Hotel", hotel.toString())

                    }
                }
                }

           runBlocking {  var hotels_search_bdd: List<Hotel>? = null

                hotels_search_bdd = hotelDaoSearch?.getHotels()
                if (!hotels_search_bdd.isNullOrEmpty()) {
                    loadingPanel.visibility = View.GONE
                    hotelsLayout.visibility = View.VISIBLE
                    recyclerViewHotels.adapter =
                        HotelsAdapter(hotels_search_bdd ?: emptyList(), list_favoris, adultsList!!)
                } else {
                    layoutNoHotelAvailable.visibility = View.VISIBLE
                }}




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
                    HotelsAdapter(hotels ?: emptyList(), list_favoris, adultsList!!)
            }


        }

    }



     @SuppressLint("RestrictedApi")
     override fun  onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                var result = data?.getStringExtra("listAdults")
                Log.d("TestLists", result)
                initializeAdultsList(result)
               if(adultsList!!.isEmpty()){
                    view?.findViewById<TextView>(R.id.room_number)?.text = "0"
                }else{
                   view?.findViewById<TextView>(R.id.room_number)?.text = adultsList?.size.toString()
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("ERREUR", "erreur")
            }
        }
    }

    private fun initializeAdultsList(result : String?){
        var substring = result?.substringBefore("]")
        substring = substring?.substringAfter("[")
        adultsList?.clear()
        if(substring?.length == 1){
            adultsList?.add(substring)
        }else{
        adultsList = substring?.split(", ") as ArrayList<String>}
    }


    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun listener_bouton(bt : Button,listButtons : List<Button>, context: Context) : Button{
        bt.setOnClickListener {

            listButtons.forEach {
                if (bt == it){
                    bt.isActivated = true
                    it.isActivated = true
                    bt.backgroundTintList =
                        context.resources!!.getColorStateList(R.color.butn_pressed)
            }else{
                    it.isActivated = false
                    it.backgroundTintList =context.resources!!.getColorStateList(R.color.white)
                }


            }
        }

        return bt

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



        private fun liste_cat_active(bt_best_seller: Button, bt_highest_first: Button, bt_lowest_first: Button, bt_price_sort: Button, bt_highest_price : Button) : String {
            var string = ""
            if(bt_best_seller.isActivated){
                string = "BEST_SELLER"

            }
            if(bt_highest_first.isActivated){
                string = "STAR_RATING_HIGHEST_FIRST"
            }
            if(bt_lowest_first.isActivated){
                string = "STAR_RATING_LOWEST_FIRST"

            }
            if(bt_price_sort.isActivated){
                string = "PRICE"

            }
            if(bt_highest_price.isActivated){
                string = "PRICE_HIGHEST_FIRST"
            }

            return string
        }

}
