package com.example.tripin.find.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.CityDao
import com.example.tripin.data.retrofit
import com.example.tripin.model.Activity
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_find_activites.activities_recyclerview
import kotlinx.android.synthetic.main.fragment_find_activity2.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.themedToolbar
import org.jetbrains.anko.toolbar


/**
 * A simple [Fragment] subclass.
 */
class FindActivityFragment : Fragment()  {

    private var activityDaoSearch : ActivityDao? = null
    private var activityDaoSaved : ActivityDao? = null
    private lateinit var citydao : CityDao
    val lang : String = "fr-FR"
    val monnaie :String = "EUR"
    var list_favoris  = arrayListOf<Boolean>()
    var list_cities_name = arrayListOf<String>()
    var city_id =""
    var categories = ""
    var query = ""
    var price_max = 100
    var price_range = "0,100"


    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // initalisation des items du layout
        val view = inflater.inflate(R.layout.fragment_find_activity2, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.activities_recyclerview)
        val bt_search = view.findViewById<Button>(R.id.bt_recherche_activity)
        val editText = view.findViewById<AutoCompleteTextView>(R.id.search_activity_bar)
        val et_query = view.findViewById<EditText>(R.id.et_query)
        val btn_museum = view.findViewById<Button>(R.id.cat_museum)
        val btn_sport = view.findViewById<Button>(R.id.cat_sport)
        val btn_food = view.findViewById<Button>(R.id.cat_food)
        val btn_fun = view.findViewById<Button>(R.id.cat_fun)
        val btn_night = view.findViewById<Button>(R.id.cat_night)
        val btn_other = view.findViewById<Button>(R.id.cat_other)
        val bt_price = view.findViewById<ImageButton>(R.id.bt_price_filter)
        val seekbar = view.findViewById<BubbleSeekBar>(R.id.seekbar)


    // initialisation recyclerview activités
        rv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        // initialisation des databases
        val databasesearch =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "searchDatabase")
                .build()
        val databasesaved =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "savedDatabase")
                .build()

        activityDaoSearch = databasesearch.getActivityDao()
        activityDaoSaved = databasesaved.getActivityDao()

        // récupération des villes possibles
        citydao = databasesaved.getCityDao()

        // ajout dans l'auto complétion texte
        runBlocking {
            val list_cities_bdd = citydao?.getCity()
            list_cities_bdd.map {
                list_cities_name.add(it.name!!)
            }
        }
        val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,list_cities_name)
        editText.setAdapter(adapter)

        // listener sur les boutons activités = change de couleur
        listener_bouton(btn_museum,requireContext())
        listener_bouton(btn_sport,requireContext())
        listener_bouton(btn_fun,requireContext())
       listener_bouton(btn_night,requireContext())
        listener_bouton(btn_food,requireContext())
        listener_bouton(btn_other,requireContext())

        // listener sur le prix
        bt_price.setOnClickListener {

            val dialog = AlertDialog.Builder(activity)
            val dialogView = layoutInflater.inflate(R.layout.layout_dialog_price,null)
            var seekbar = dialogView.findViewById<BubbleSeekBar>(R.id.seekbar)
            seekbar.setProgress(price_max.toFloat())
            dialog.setView(dialogView)
            dialog.setCancelable(false)
            dialog.setPositiveButton(android.R.string.ok) { dialog, which ->
                price_range = "0,${seekbar.progress}"
                price_max = seekbar.progress
            }
            dialog.show()



        }


        // Lancement de la recherche
      bt_search.setOnClickListener {

          // supression des anciens éléments (list_fav + list_activité)
          runBlocking {
              activityDaoSearch?.deleteActivity()
              list_favoris.clear()
          }

          // récupère la ville saisie
            val city_name = editText.text.toString()
            val service = retrofit().create(ActivitybyCity::class.java)
            runBlocking {
                // Récupère les données des items du layout
                val city = citydao?.getCity(city_name)
                if (city != null) {
                    city_id = city.id.toString()
                }
                query = et_query.text.toString()
                categories = liste_cat_active(btn_museum,btn_food,btn_night,btn_fun,btn_other,btn_sport)

                //lancement de la requête api
                val result = service.listAct(query,"AUTO","LEVEL-0","relevance-city",city_id,price_range,categories,"20",lang,monnaie)

                //Traitement du résultat
                if (result.meta.count != 0L) {
                    layoutNoActivities_frag.visibility = View.GONE
                    val list_activities_bdd = activityDaoSaved?.getActivity()
                    result.data.map {
                        val titre = it.title
                        var match_bdd = false
                        list_activities_bdd?.forEach {
                            if (it.title == titre) {
                                list_favoris.add(true)
                                match_bdd = true
                            }
                        }
                        if (match_bdd == false) {
                            list_favoris.add(false)
                        }
                        val list_cat =  it.categories.map {
                            it.name
                        }


                        val activity = Activity(
                            it.uuid,
                            it.title,
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
                activities_recyclerview.adapter =
                    ActivityAdapterGlobal(activities ?: emptyList(), list_favoris)
            }
                else {
                    layoutNoActivities_frag.visibility = View.VISIBLE
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
        activityDaoSearch = databasesearch.getActivityDao()
        activityDaoSaved = databasesaved.getActivityDao()

        runBlocking {
            val activities = activityDaoSearch?.getActivity()
            val list_activities_bdd = activityDaoSaved?.getActivity()

            activities?.map {
                    val titre = it.title
                    var match_bdd = false
                    list_activities_bdd?.forEach {
                        if (it.title == titre) {
                            list_favoris.add(true)
                            match_bdd = true
                        }
                    }
                    if (match_bdd == false) {
                        list_favoris.add(false)
                    }
                activities_recyclerview.adapter =
                    ActivityAdapterGlobal(activities ?: emptyList(), list_favoris)
            }


        }
    }
}

private fun listener_bouton(bt : Button,context: Context) : Button{
    bt.setOnClickListener {
        if (bt.isActivated) {
            bt.isActivated = false
            bt.backgroundTintList =
                context.getResources()!!.getColorStateList(R.color.white)
        } else {
            bt.isActivated = true
            bt.backgroundTintList =
                context.getResources()!!.getColorStateList(R.color.butn_pressed)
        }
    }

    return bt

}

private fun liste_cat_active(bt_musee : Button,bt_food : Button,bt_night : Button,bt_fun : Button,bt_other : Button,bt_sport:Button) : String {
    var string = ""
    var premier_item = true

    if(bt_musee.isActivated){
        if(premier_item == true) {
            string += "arts-culture"
            premier_item = false} else {string += ",arts-culture"}
    }
    if(bt_food.isActivated){
        if(premier_item == true) {
            string += "food-wine"
            premier_item = false} else {string += ",food_wine"}
    }
    if(bt_night.isActivated){
        if(premier_item == true) {
            string += "nightlife"
            premier_item = false} else {string += ",nightlife"}
    }
    if(bt_fun.isActivated){
        if(premier_item == true) {
            string += "entertainment"
            premier_item = false} else {string += ",entertainement"}
    }
    if(bt_other.isActivated){
        if(premier_item == true) {
            string += "sightseeing"
            premier_item = true} else {string += ",sightseeing"}
    }
    if(bt_sport.isActivated){
        if(premier_item == true) {
            //string += "adventure2Csports"
            string += "sports"
            premier_item = false} else {string += ",adventure,sports"}
    }

    return string
}