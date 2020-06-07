package com.example.tripin.home

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.*
import com.example.tripin.find.activity.ActivityAdapterGlobalFormatted
import com.example.tripin.find.activity.ActivitybyCity
import com.example.tripin.find.hotel.HotelAPI
import com.example.tripin.find.hotel.HotelsResizedAdapter
import com.example.tripin.find.hotel.ModelRapid
import com.example.tripin.model.Activity
import com.example.tripin.model.City
import com.example.tripin.model.Hotel
import kotlinx.coroutines.runBlocking


class HomeFragment : Fragment() {

    private var activityDaoSearch: ActivityDao? = null
    private var activityDaoSaved: ActivityDao? = null
    private var preferenceDao: PreferenceDao? = null
    val lang: String = "fr-FR"
    val monnaie: String = "EUR"
    var city_query: String = "Madrid"
    private lateinit var citydao: CityDao
    var list_destination : List<City> = emptyList()
    var list_favoris = arrayListOf<Boolean>()
    var budgetPref: Int = 100

    private var hotelDaoSearch: HotelDao? = null
    private var hotelDaoSaved: HotelDao? = null
    private val hotelKey = "5f672e716bmsh702ca7444dd484cp121785jsn039c3a4937f8"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        var recyclerview_home = root.findViewById(R.id.activities_recyclerview_home) as RecyclerView
        recyclerview_home.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        var noActivity_home = root.findViewById(R.id.layoutNoActivities_frag_home) as RelativeLayout



        val databasesearch =
            Room.databaseBuilder(
                requireActivity().baseContext,
                AppDatabase::class.java,
                "homeDatabase"
            )
                .build()
        val databasesaved =
            Room.databaseBuilder(
                requireActivity().baseContext,
                AppDatabase::class.java,
                "savedDatabase"
            )
                .build()

        val database = Room.databaseBuilder(
            requireActivity(),
            AppDatabase::class.java,
            "allpreferences"
        ).build()

        preferenceDao = database.getPreferenceDao()
        activityDaoSearch = databasesearch.getActivityDao()
        activityDaoSaved = databasesaved.getActivityDao()
        citydao = databasesaved.getCityDao()


        runBlocking {
            val city_pref = preferenceDao?.getPreference()
            activityDaoSearch?.deleteActivity()
            list_favoris.clear()
            Log.d("KLM", "${city_pref?.destination}")
            city_query = city_pref?.destination ?: "Madrid"
            budgetPref = city_pref?.budget ?: 100
            Log.d("KLM", "$budgetPref")
            val envie = city_pref?.envie ?: "Au soleil"
            Log.d("tyui", "$envie")
            list_destination = citydao.getCityByDestination(envie)
        }
        // récupère la ville saisie
        val city_name = city_query
        Log.d("tyui", "$city_query")
        val service = retrofit().create(ActivitybyCity::class.java)
        runBlocking {
            val city = citydao?.getCity(city_name)
            if (city != null) {
                var result = service.listActivitybyCity(city.id, "relevance", "", lang, monnaie)
                noActivity_home.visibility = View.GONE

                val list_activities_bdd = activityDaoSaved?.getActivity()
                result.data.map {
                    val titre = it.title
                    var match_bdd = false
                    // vérification pour le bouton favoris
                    list_activities_bdd?.forEach {
                        if (it.title == titre) {
                            list_favoris.add(true)
                            match_bdd = true
                        }
                    }
                    if (match_bdd == false) {
                        list_favoris.add(false)
                    }


                    var list_cat = it.categories.map {
                        it.name
                    }


                    val activity = Activity(
                        it.uuid,
                        it.title,
                        it.city.name,
                        it.cover_image_url,
                        it.retail_price.formatted_iso_value,
                        it.operational_days,
                        it.reviews_avg,
                        list_cat,
                        it.url,
                        it.top_seller,
                        it.must_see,
                        it.description,
                        it.about,
                        it.latitude,
                        it.longitude
                    )
                    activityDaoSearch?.addActivity(activity)

                }
                val activities = activityDaoSearch?.getActivity()
                recyclerview_home.adapter =
                    ActivityAdapterGlobalFormatted(activities!!.toMutableList(), list_favoris)
            }
            if(city == null || (city == null && list_destination.isEmpty())){
                Log.d("tyui", "ca marche mais c'est null")
                Toast.makeText(requireContext(), "La ville que vous avez saisie n'est pas reconnue", Toast.LENGTH_SHORT).show()
                noActivity_home.visibility = View.VISIBLE

            }
            if(city == null && list_destination.isNotEmpty()){
                for (x in list_destination.indices){
                    var result = service.listActivitybyCity(list_destination[x].id, "relevance", "", lang, monnaie)
                    noActivity_home.visibility = View.GONE

//                if(btn_museum_activate.isActivated||btn_sport_activate.isActivated||btn_night_activate.isActivated||btn_food_activate.isActivated||btn_fun_activate.isActivated||btn_other_activate.isActivated){
//                    var categories = liste_cat_active(btn_museum,btn_food,btn_night,btn_fun,btn_other,btn_sport)
//                    result = service.listActivitybyCityandCategory(city.id,categories,lang, monnaie)
//                }

//                    if (result.meta.count == 0L) {
//                        noActivity_home.visibility = View.VISIBLE
//                    } else {
//                        noActivity_home.visibility = View.GONE
//                    }

                    val list_activities_bdd = activityDaoSaved?.getActivity()
                    result.data.map {
                        val titre = it.title
                        var match_bdd = false
                        // vérification pour le bouton favoris
                        list_activities_bdd?.forEach {
                            if (it.title == titre) {
                                list_favoris.add(true)
                                match_bdd = true
                            }
                        }
                        if (match_bdd == false) {
                            list_favoris.add(false)
                        }


                        var list_cat = it.categories.map {
                            it.name
                        }


                        val activity = Activity(
                            it.uuid,
                            it.title,
                            it.city.name,
                            it.cover_image_url,
                            it.retail_price.formatted_iso_value,
                            it.operational_days,
                            it.reviews_avg,
                            list_cat,
                            it.url,
                            it.top_seller,
                            it.must_see,
                            it.description,
                            it.about,
                            it.latitude,
                            it.longitude
                        )
                        activityDaoSearch?.addActivity(activity)
                    }
                }
                val activities = activityDaoSearch?.getActivity()
                recyclerview_home.adapter =
                    ActivityAdapterGlobalFormatted(activities!!.toMutableList(), list_favoris)
            }
//            else {
////                noActivity_home.visibility = View.VISIBLE
//                Toast.makeText(requireContext(), "La ville que vous avez saisie n'est pas reconnue", Toast.LENGTH_SHORT).show()
//            }
        }

        var recyclerview_hotel = root.findViewById(R.id.hotels_recyclerview) as RecyclerView
        recyclerview_hotel.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        //Initialisation Database
        val databaseSearch =
            Room.databaseBuilder(
                requireActivity().baseContext,
                AppDatabase::class.java,
                "searchDatabase"
            ).build()
        val databaseSaved =
            Room.databaseBuilder(
                requireActivity().baseContext,
                AppDatabase::class.java,
                "savedDatabase"
            ).build()

        hotelDaoSaved = databaseSaved.getHotelDao()
        citydao = databaseSaved.getCityDao()
        hotelDaoSearch = databaseSearch.getHotelDao()

        val dateArrivee = "2020-06-09"
        val dateDepart = "2020-06-14"
        val serviceBis = retrofitHotel().create(HotelAPI::class.java)
        //Récupération du filtre de recherche
        var sortBy : String = ""
        var cityCode: Int= 0
        //var adultsList : ArrayList<String> ?= arrayListOf()
        val priceMinChosen = 10
        val priceMaxChosen = budgetPref
        var adultsList = 1

        sortBy =
            liste_cat_active("", "", "bt_lowest_first", "", "")

        //Recuperation du code de la ville de l'API
        runBlocking {
            hotelDaoSearch?.deleteHotels()
            val searchCity = "Paris"
            var result = serviceBis.getLocation("fr_FR",searchCity.toLowerCase(),hotelKey)

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
            var resultH: ModelRapid.Hotels? = null
//            when (adultsList?.size) {
                if(adultsList == 1){
                    resultH = serviceBis.getHotelsList(cityCode,1, dateArrivee, dateDepart,10,
                        1, null,null, null,
                        sortBy,priceMinChosen, priceMaxChosen,"fr_FR","EUR",hotelKey )
                }
            if(adultsList == 2){
                    resultH = serviceBis.getHotelsList(cityCode,1, dateArrivee, dateDepart,10,
                        1,  2,null, null,
                        sortBy,priceMinChosen, priceMaxChosen,"fr_FR","EUR",hotelKey )
                }
            if(adultsList == 3){
                    resultH = serviceBis.getHotelsList(cityCode,1, dateArrivee, dateDepart,10,
                        1,  2,3, null,
                        sortBy,priceMinChosen, priceMaxChosen,"fr_FR","EUR",hotelKey )
                }
            if(adultsList == 4){
                    resultH = serviceBis.getHotelsList(cityCode,1, dateArrivee, dateDepart,10,
                        1, 2,3, 4,
                        sortBy,priceMinChosen, priceMaxChosen,"fr_FR","EUR",hotelKey)
                }
//                else -> {
//                    Toast.makeText(requireActivity().baseContext, "Veuillez ajouter des chambres", Toast.LENGTH_SHORT).show()
//                }
//            }

            Log.d("Hotel", "Récupération des données terminée")



            resultH?.data?.body?.searchResults?.results?.map {
                var idHotel = it.id.toString()
                var adresse: MutableList<String> = mutableListOf()
                val geocoder = Geocoder(activity)
                val adresseList = geocoder.getFromLocation(
                    it.coordinate.lat,
                    it.coordinate.lon,
                    1
                )


                //Gestion de l'adresse
                adresseList.map {
                    adresse.add(it.featureName)
                    if (it.thoroughfare == null) {
                        adresse.add("null")
                    } else {
                        adresse.add(it.thoroughfare)
                    }

                    adresse.add(it.postalCode)
                    if(it.locality == null){
                        adresse.add("null")
                    } else{
                        adresse.add(it.locality)
                    }

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
                    it.id.toInt(),
                    it.name,
                    null,
                    it.starRating.toInt(),
                    it.thumbnailUrl,
                    adresse,
                    it.coordinate.lat,
                    it.coordinate.lon,
                    it.ratePlan.price.current,
                    null
                )

                hotelDaoSearch?.addHotel(hotel)
                Log.d("Hotel", hotel.toString())

            }
        }

        runBlocking {  var hotels_search_bdd: List<Hotel>? = null
            var adultsList : ArrayList<String> ?= arrayListOf()
            hotels_search_bdd = hotelDaoSearch?.getHotels()
            if (!hotels_search_bdd.isNullOrEmpty()) {
//                loadingPanel.visibility = View.GONE
//                hotelsLayout.visibility = View.VISIBLE
                recyclerview_hotel.adapter =
                    HotelsResizedAdapter(hotels_search_bdd ?: emptyList(), list_favoris, adultsList!!, dateArrivee, dateDepart)
            }
//            else {
//                layoutNoHotelAvailable.visibility = View.VISIBLE
//            }
        }
        return root
    }

    private fun liste_cat_active(bt_best_seller: String, bt_highest_first: String, bt_lowest_first: String, bt_price_sort: String, bt_highest_price : String) : String {
        var string = ""
        if(bt_best_seller.isNotEmpty()){
            string = "BEST_SELLER"

        }
        if(bt_highest_first.isNotEmpty()){
            string = "STAR_RATING_HIGHEST_FIRST"
        }
        if(bt_lowest_first.isNotEmpty()){
            string = "STAR_RATING_LOWEST_FIRST"

        }
        if(bt_price_sort.isNotEmpty()){
            string = "PRICE"

        }
        if(bt_highest_price.isNotEmpty()){
            string = "PRICE_HIGHEST_FIRST"
        }

        return string
    }
}

