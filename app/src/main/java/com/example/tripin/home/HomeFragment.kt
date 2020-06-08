package com.example.tripin.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*


class HomeFragment : Fragment() {

    private var activityDaoSearch: ActivityDao? = null
    private var activityDaoSaved: ActivityDao? = null
    private var preferenceDao: PreferenceDao? = null
    private val lang: String = "fr-FR"
    private val monnaie: String = "EUR"
    private var cityQuery: String = "Madrid"
    private lateinit var citydao: CityDao
    private var listDestination: List<City> = emptyList()
    private var listFavorishotel = arrayListOf<Boolean>()
    private var listFavorisAct = arrayListOf<Boolean>()
    private var budgetPref: Int = 100
    private var cityPref: Preference? = null
    private var catactivite : String? = ""
    private var hotelDaoSearch: HotelDao? = null
    private var hotelDaoSaved: HotelDao? = null
    private val hotelKey = "91ebc5f73emsh3e988c6d88395f9p15ab7fjsnb347a2c046b3"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val progressOverlay = root.findViewById<FrameLayout>(R.id.progress_overlay)
        animateView(progressOverlay, View.VISIBLE, 0.4f, 200)


        val recyclerviewHome = root.findViewById(R.id.activities_recyclerview_home) as RecyclerView
        recyclerviewHome.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val noActivityHome = root.findViewById(R.id.layoutNoActivities_frag_home) as RelativeLayout
        val recyclerviewHotel = root.findViewById(R.id.hotels_recyclerview) as RecyclerView
        recyclerviewHotel.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


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

        AsyncTask.execute {

            preferenceDao = database.getPreferenceDao()
            activityDaoSearch = databaseSearch.getActivityDao()
            activityDaoSaved = databaseSaved.getActivityDao()
            citydao = databaseSaved.getCityDao()


            runBlocking {
                cityPref = preferenceDao?.getPreference()
                cityQuery = cityPref?.destination ?: "Madrid"
                budgetPref = cityPref?.budget ?: 100
                val envie = cityPref?.envie ?: "Au soleil"
                catactivite = cityPref?.souhait
                if(catactivite.isNullOrEmpty()){catactivite =""}
                listDestination = citydao.getCityByDestination(envie)
            }
            // récupère la ville saisie
            val cityName = cityQuery
            var activitySearch: List<Activity>? = emptyList()
            runBlocking {
                activitySearch = activityDaoSearch?.getActivity()
            }

            if (activitySearch.isNullOrEmpty()) {

                listFavorisAct.clear()

                val service = retrofit().create(ActivitybyCity::class.java)
                runBlocking {
                    val city = citydao.getCity(cityName)
                    val result = service.listActivitybyCity(city.id, "relevance", catactivite!!, lang, monnaie)
                    noActivityHome.visibility = View.GONE

                    val listActivitiesBdd = activityDaoSaved?.getActivity()
                    result.data.map {
                        val titre = it.title
                        var matchBdd = false
                        // vérification pour le bouton favoris
                        listActivitiesBdd?.forEach { itL ->
                            if (itL.title == titre) {
                                listFavorisAct.add(true)
                                matchBdd = true
                            }
                        }
                        if (!matchBdd) {
                            listFavorisAct.add(false)
                        }

                        val listCat = it.categories.map { itC ->
                            itC.name
                        }

                        val activity = Activity(
                            it.uuid,
                            it.title,
                            it.city.name,
                            it.cover_image_url,
                            it.retail_price.formatted_iso_value,
                            it.operational_days,
                            it.reviews_avg,
                            listCat,
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
                    withContext(Dispatchers.Main) {
                        recyclerviewHome.adapter =
                            ActivityAdapterGlobalFormatted(
                                activities!!.toMutableList(),
                                listFavorisAct
                            )
                    }
                }
            } else {

                runBlocking {
                    val listActivitiesBdd = activityDaoSaved?.getActivity()

                    activitySearch?.map {
                        val titre = it.title
                        var matchBdd = false
                        // vérification pour le bouton favoris
                        listActivitiesBdd?.forEach { itA ->
                            if (itA.title == titre) {
                                listFavorisAct.add(true)
                                matchBdd = true
                            }
                        }
                        if (!matchBdd) {
                            listFavorisAct.add(false)
                        }

                        withContext(Dispatchers.Main) {

                            recyclerviewHome.adapter =
                                ActivityAdapterGlobalFormatted(
                                    activitySearch!!.toMutableList(),
                                    listFavorisAct
                                )
                        }
                    }
                }
            }




            hotelDaoSaved = databaseSaved.getHotelDao()
            hotelDaoSearch = databaseSearch.getHotelDao()

            var topo: List<Hotel>? = emptyList()

            runBlocking {
                topo = hotelDaoSearch?.getHotels()
            }

            val dateArrivee = "2020-06-18"
            val dateDepart = "2020-06-25"

            //Récupération du filtre de recherche
            val sortBy: String
            var cityCode = 0
            val priceMinChosen = 10
            val priceMaxChosen = budgetPref
            val adultsList = 1

            if (topo.isNullOrEmpty()) {

                val serviceBis = retrofitHotel().create(HotelAPI::class.java)

                sortBy =
                    listeCatActive("", "", "bt_lowest_first", "", "")

                //Recuperation du code de la ville de l'API
                runBlocking {
                    hotelDaoSearch?.deleteHotels()
                    val city = citydao.getCity(cityName)
                    val searchCity = city.name ?: "Paris"


                    val result = serviceBis.getLocation("fr_FR", searchCity.toLowerCase(Locale.ROOT), hotelKey)

                    result.suggestions.map {
                        if (it.group == "CITY_GROUP") {
                            cityCode = it.entities[0].destinationId.toInt()
                        }
                    }
                }

                //Lancement de la recherche
                runBlocking {
                    hotelDaoSearch?.deleteHotels()
                    val hotelsSavedBdd = hotelDaoSaved?.getHotels()
                    listFavorishotel.clear()


                    Log.d("Hotel", "Début de la récupération des données")
                    var resultH: ModelRapid.Hotels? = null

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
                        val idHotel = it.id.toString()
                        val adresse: MutableList<String> = mutableListOf()
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
                        var matchBdd = false
                        hotelsSavedBdd?.forEach { itH ->
                            if (itH.hotelId == idHotel.toInt()) {
                                listFavorishotel.add(true)
                                matchBdd = true
                            }
                        }
                        if (!matchBdd) {
                            listFavorishotel.add(false)
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

                    val adultsListE: ArrayList<String>? = arrayListOf()
                    withContext(Dispatchers.Main) {

                        recyclerviewHotel.adapter =
                            HotelsResizedAdapter(
                                listH ?: emptyList(),
                                listFavorishotel,
                                adultsListE!!,
                                dateArrivee,
                                dateDepart
                            )

                        animateView(progressOverlay, View.GONE, 0f, 200)
//                        root.layout_recyclerView.visibility = View.VISIBLE

                    }
                }

            } else {
                runBlocking {
                    topo?.map {
                        val hotelsSavedBdd = hotelDaoSaved?.getHotels()
                        //Récupération des hôtels favoris
                        var matchBdd = false
                        hotelsSavedBdd?.forEach { itH ->
                            if (it.hotelId == itH.hotelId) {
                                listFavorishotel.add(true)
                                matchBdd = true
                            }
                        }
                        if (!matchBdd) {
                            listFavorishotel.add(false)
                        }
                    }
                    val adultsListE: ArrayList<String>? = arrayListOf()
                    withContext(Dispatchers.Main) {

                        recyclerviewHotel.adapter =
                            HotelsResizedAdapter(
                                topo ?: emptyList(),
                                listFavorishotel,
                                adultsListE!!,
                                dateArrivee,
                                dateDepart
                            )
                        animateView(progressOverlay, View.GONE, 0f, 200)
//                        root.layout_recyclerView.visibility = View.VISIBLE
                    }
                }
            }

        }

        return root
    }

    private fun listeCatActive(
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

    private fun animateView(
        view: View,
        toVisibility: Int,
        toAlpha: Float,
        duration: Int
    ) {
        val show = toVisibility == View.VISIBLE
        if (show) {
            view.alpha = 0f
        }
        view.visibility = View.VISIBLE
        view.animate()
            .setDuration(duration.toLong())
            .alpha(if (show) toAlpha else 0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = toVisibility
                }
            })
    }
}

