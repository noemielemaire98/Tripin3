package com.example.tripin.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.*
import com.example.tripin.find.IgnoreAccentsArrayAdapter
import com.example.tripin.find.activity.ActivityAdapterGlobal
import com.example.tripin.find.activity.ActivityAdapterGlobalFormatted
import com.example.tripin.find.activity.ActivitybyCity
import com.example.tripin.model.Activity
import com.example.tripin.model.City
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
            val envie = city_pref?.envie ?: "Au soleil"
            Log.d("tyui", "$envie")
            list_destination = citydao.getCityByDestination(envie)
        }

        // récupère la ville saisie
        val city_name = city_query
        val service = retrofit().create(ActivitybyCity::class.java)
        runBlocking {
            val city = citydao?.getCity(city_name)
            if (city != null) {


                var result = service.listActivitybyCity(city.id, "relevance", "", lang, monnaie)

//                if(btn_museum_activate.isActivated||btn_sport_activate.isActivated||btn_night_activate.isActivated||btn_food_activate.isActivated||btn_fun_activate.isActivated||btn_other_activate.isActivated){
//                    var categories = liste_cat_active(btn_museum,btn_food,btn_night,btn_fun,btn_other,btn_sport)
//                    result = service.listActivitybyCityandCategory(city.id,categories,lang, monnaie)
//                }

                if (result.meta.count == 0L) {
                    noActivity_home.visibility = View.VISIBLE
                } else {
                    noActivity_home.visibility = View.GONE
                }

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
            if(city == null && list_destination.isNotEmpty()){
                for (x in list_destination.indices){
                    var result = service.listActivitybyCity(list_destination[x].id, "relevance", "", lang, monnaie)

//                if(btn_museum_activate.isActivated||btn_sport_activate.isActivated||btn_night_activate.isActivated||btn_food_activate.isActivated||btn_fun_activate.isActivated||btn_other_activate.isActivated){
//                    var categories = liste_cat_active(btn_museum,btn_food,btn_night,btn_fun,btn_other,btn_sport)
//                    result = service.listActivitybyCityandCategory(city.id,categories,lang, monnaie)
//                }

                    if (result.meta.count == 0L) {
                        noActivity_home.visibility = View.VISIBLE
                    } else {
                        noActivity_home.visibility = View.GONE
                    }

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
            else {
                noActivity_home.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "La ville que vous avez saisie n'est pas reconnue", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

}

