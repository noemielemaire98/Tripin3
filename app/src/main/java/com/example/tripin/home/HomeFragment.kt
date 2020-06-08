package com.example.tripin.home

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
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
import com.example.tripin.model.Preference
import kotlinx.coroutines.runBlocking


class HomeFragment : Fragment() {

    private var activityDaoSearch: ActivityDao? = null
    private var activityDaoSaved: ActivityDao? = null
    private var preferenceDao: PreferenceDao? = null
    val lang: String = "fr-FR"
    val monnaie: String = "EUR"
    var city_query: String = "Madrid"
    private lateinit var citydao: CityDao
    var list_destination: List<City> = emptyList()
    var list_favorisHotel = arrayListOf<Boolean>()
    var listFavorisAct = arrayListOf<Boolean>()
    var budgetPref: Int = 100
    private var city_pref: Preference? = null

    private var hotelDaoSearch: HotelDao? = null
    private var hotelDaoSaved: HotelDao? = null
    private val hotelKey = "9a6f295efemsh9dd64f537c1e62bp194635jsn1c7a940b93ba"

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


        val databaseSearch =
            Room.databaseBuilder(
                requireActivity().baseContext,
                AppDatabase::class.java,
                "homeDatabase"
            ).build()

        val databaseSaved =
            Room.databaseBuilder(
                requireActivity().baseContext,
                AppDatabase::class.java,
                "savedDatabase"
            ).build()

        val database = Room.databaseBuilder(
            requireActivity(),
            AppDatabase::class.java,
            "allpreferences"
        ).build()

        preferenceDao = database.getPreferenceDao()
        activityDaoSearch = databaseSearch.getActivityDao()
        activityDaoSaved = databaseSaved.getActivityDao()
        citydao = databaseSaved.getCityDao()


        runBlocking {
            city_pref = preferenceDao?.getPreference()
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
        var activitySearch: List<Activity>? = emptyList()
        runBlocking {
            activitySearch = activityDaoSearch?.getActivity()
        }

        if (activitySearch.isNullOrEmpty()) {

            listFavorisAct.clear()

            val service = retrofit().create(ActivitybyCity::class.java)
            runBlocking {
                val city = citydao.getCity(city_name)
                val result = service.listActivitybyCity(city.id, "relevance", "", lang, monnaie)
                noActivity_home.visibility = View.GONE

                val list_activities_bdd = activityDaoSaved?.getActivity()
                result.data.map {
                    val titre = it.title
                    var match_bdd = false
                    // vérification pour le bouton favoris
                    list_activities_bdd?.forEach {
                        if (it.title == titre) {
                            listFavorisAct.add(true)
                            match_bdd = true
                        }
                    }
                    if (!match_bdd) {
                        listFavorisAct.add(false)
                    }


                    val list_cat = it.categories.map {
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
                    ActivityAdapterGlobalFormatted(activities!!.toMutableList(), listFavorisAct)

            }
        } else {

            runBlocking {
                val list_activities_bdd = activityDaoSaved?.getActivity()


                activitySearch?.map {
                    val titre = it.title
                    var match_bdd = false
                    // vérification pour le bouton favoris
                    list_activities_bdd?.forEach {
                        if (it.title == titre) {
                            listFavorisAct.add(true)
                            match_bdd = true
                        }
                    }
                    if (!match_bdd) {
                        listFavorisAct.add(false)
                    }

                    recyclerview_home.adapter =
                        ActivityAdapterGlobalFormatted(
                            activitySearch!!.toMutableList(),
                            listFavorisAct
                        )
                }
            }
        }

        var recyclerview_hotel = root.findViewById(R.id.hotels_recyclerview) as RecyclerView
        recyclerview_hotel.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        hotelDaoSaved = databaseSaved.getHotelDao()
        hotelDaoSearch = databaseSearch.getHotelDao()

        var topo: List<Hotel>? = emptyList()

        runBlocking {
            topo = hotelDaoSearch?.getHotels()
        }

        val dateArrivee = "2020-06-18"
        val dateDepart = "2020-06-25"

        //Récupération du filtre de recherche
        var sortBy: String = ""
        var cityCode: Int = 0
        //var adultsList : ArrayList<String> ?= arrayListOf()
        val priceMinChosen = 10
        val priceMaxChosen = budgetPref
        val adultsList = 1

        if (topo.isNullOrEmpty()) {

            val serviceBis = retrofitHotel().create(HotelAPI::class.java)

            sortBy =
                liste_cat_active("", "", "bt_lowest_first", "", "")

            //Recuperation du code de la ville de l'API
            runBlocking {
                hotelDaoSearch?.deleteHotels()
                val city = citydao.getCity(city_name)
                val searchCity = city.name ?: "Paris"


                var result = serviceBis.getLocation("fr_FR", searchCity.toLowerCase(), hotelKey)

                result.suggestions.map {
                    if (it.group == "CITY_GROUP") {
                        cityCode = it.entities[0].destinationId.toInt()
                    }
                }
            }

            //Lancement de la recherche
            runBlocking {
                hotelDaoSearch?.deleteHotels()
                val hotels_saved_bdd = hotelDaoSaved?.getHotels()
                list_favorisHotel.clear()


                Log.d("Hotel", "Début de la récupération des données")
                var resultH: ModelRapid.Hotels? = null
//            when (adultsList?.size) {
                //Gestion de l'adresse


                //Récupération des hôtels favoris
                when (adultsList) {
                    1 -> {
                        resultH = serviceBis.getHotelsList(
                            cityCode, 1, dateArrivee, dateDepart, 10,
                            1, null, null, null,
                            sortBy, priceMinChosen, priceMaxChosen, "fr_FR", "EUR", hotelKey
                        )
                    }
                    2 -> {
                        resultH = serviceBis.getHotelsList(
                            cityCode, 1, dateArrivee, dateDepart, 10,
                            1, 2, null, null,
                            sortBy, priceMinChosen, priceMaxChosen, "fr_FR", "EUR", hotelKey
                        )
                    }
                    3 -> {
                        resultH = serviceBis.getHotelsList(
                            cityCode, 1, dateArrivee, dateDepart, 10,
                            1, 2, 3, null,
                            sortBy, priceMinChosen, priceMaxChosen, "fr_FR", "EUR", hotelKey
                        )
                    }
                    4 -> {
                        resultH = serviceBis.getHotelsList(
                            cityCode, 1, dateArrivee, dateDepart, 10,
                            1, 2, 3, 4,
                            sortBy, priceMinChosen, priceMaxChosen, "fr_FR", "EUR", hotelKey
                        )
                    }
                }

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
                    adresseList.map { itH ->
                        adresse.add(itH.featureName)
                        if (itH.thoroughfare == null) {
                            adresse.add("null")
                        } else {
                            adresse.add(itH.thoroughfare)
                        }

                        adresse.add(itH.postalCode)
                        if (itH.locality == null) {
                            adresse.add("null")
                        } else {
                            adresse.add(itH.locality)
                        }

                        adresse.add(itH.countryName)
                    }


                    //Récupération des hôtels favoris
                    var match_bdd = false
                    var favori = false
                    hotels_saved_bdd?.forEach {
                        if (it.hotelId == idHotel.toInt()) {
                            list_favorisHotel.add(true)
                            match_bdd = true
                            favori = true
                        }
                    }
                    if (!match_bdd) {
                        list_favorisHotel.add(false)
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
                val listH = hotelDaoSearch?.getHotels()

                val adultsList: ArrayList<String>? = arrayListOf()
                recyclerview_hotel.adapter =
                    HotelsResizedAdapter(
                        listH ?: emptyList(),
                        list_favorisHotel,
                        adultsList!!,
                        dateArrivee,
                        dateDepart
                    )
            }

        } else {
            runBlocking {
                topo?.map {
                    val hotels_saved_bdd = hotelDaoSaved?.getHotels()
                    //Récupération des hôtels favoris
                    var match_bdd = false
                    hotels_saved_bdd?.forEach { itH ->
                        if (it.hotelId == itH.hotelId) {
                            list_favorisHotel.add(true)
                            match_bdd = true
                        }
                    }
                    if (!match_bdd) {
                        list_favorisHotel.add(false)
                    }
                }
                val adultsList: ArrayList<String>? = arrayListOf()
                recyclerview_hotel.adapter =
                    HotelsResizedAdapter(
                        topo ?: emptyList(),
                        list_favorisHotel,
                        adultsList!!,
                        dateArrivee,
                        dateDepart
                    )
            }
        }

        runBlocking {
            val adultsList: ArrayList<String>? = arrayListOf()
            if (!topo.isNullOrEmpty()) {
//                loadingPanel.visibility = View.GONE
//                hotelsLayout.visibility = View.VISIBLE
                recyclerview_hotel.adapter =
                    HotelsResizedAdapter(
                        topo ?: emptyList(),
                        list_favorisHotel,
                        adultsList!!,
                        dateArrivee,
                        dateDepart
                    )
            }
//            else {
//                layoutNoHotelAvailable.visibility = View.VISIBLE
//            }
        }
        return root
    }

    private fun liste_cat_active(
        bt_best_seller: String,
        bt_highest_first: String,
        bt_lowest_first: String,
        bt_price_sort: String,
        bt_highest_price: String
    ): String {
        var string = ""
        if (bt_best_seller.isNotEmpty()) {
            string = "BEST_SELLER"

        }
        if (bt_highest_first.isNotEmpty()) {
            string = "STAR_RATING_HIGHEST_FIRST"
        }
        if (bt_lowest_first.isNotEmpty()) {
            string = "STAR_RATING_LOWEST_FIRST"

        }
        if (bt_price_sort.isNotEmpty()) {
            string = "PRICE"

        }
        if (bt_highest_price.isNotEmpty()) {
            string = "PRICE_HIGHEST_FIRST"
        }

        return string
    }
}

