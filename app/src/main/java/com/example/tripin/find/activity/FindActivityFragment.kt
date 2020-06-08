package com.example.tripin.find.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.example.tripin.R
import com.example.tripin.data.*
import com.example.tripin.model.Activity
import com.example.tripin.model.Voyage
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_find_activites.activities_recyclerview
import kotlinx.android.synthetic.main.fragment_find_activity2.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*


class FindActivityFragment : Fragment()  {

    private var mBundleRecyclerViewState: Bundle? = null
    private var mListState: Parcelable? = null
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
    var aller_date= ""
    var retour_date= ""


    var voyage: Voyage?=null
    var voyageDao : VoyageDao? = null


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
        val btn_museum = view.findViewById<Button>(R.id.cat_museum)
        val btn_sport = view.findViewById<Button>(R.id.cat_sport)
        val btn_food = view.findViewById<Button>(R.id.cat_food)
        val btn_fun = view.findViewById<Button>(R.id.cat_fun)
        val btn_night = view.findViewById<Button>(R.id.cat_night)
        val btn_other = view.findViewById<Button>(R.id.cat_other)
        val bt_price = view.findViewById<ImageButton>(R.id.bt_price_filter)
        val bt_date = view.findViewById<ImageButton>(R.id.bt_date_filter)

        //Si arriver depuis detailsvoyage
        var id = arguments?.getInt("id")
        Log.d("zzz", "id findvoyage =$id")

        if (id != null) {
            val database =
                Room.databaseBuilder(requireContext(), AppDatabase::class.java, "savedDatabase")
                    .build()
            voyageDao = database.getVoyageDao()
            runBlocking {
                voyage = voyageDao!!.getVoyage(id)
                Log.d("zzz", "voyage1 = $voyage")
            }

            if (voyage != null){
                editText.text = SpannableStringBuilder(voyage!!.destination)
                aller_date = voyage!!.date.toString()
                retour_date = voyage!!.dateRetour.toString()
            }

        }


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

        //listener sur date
        bt_date.setOnClickListener {
            val rangeDaysPickCallback = RangeDaysPickCallback { startDate, endDate ->
                Log.d("Date", "${startDate.shortDateString} ${endDate.shortDateString}")
                val parser =
                    SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val formatterDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                aller_date =
                    formatterDate.format(parser.parse(startDate.shortDateString)!!)
               retour_date =
                    formatterDate.format(parser.parse(endDate.shortDateString)!!)
            }

            val today = CivilCalendar()

            if (aller_date != "" && retour_date != "") {
                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calStart = CivilCalendar()
                calStart.timeInMillis = df.parse(aller_date)!!.time
                val calEnd = CivilCalendar()
                calEnd.timeInMillis = df.parse(retour_date)!!.time

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

        // Lancement de la recherche
       bt_search.setOnClickListener {

          // supression des anciens éléments (list_fav + list_activité)
          runBlocking {
              activityDaoSearch?.deleteActivity()
              list_favoris.clear()
              city_id =""
              query = ""
          }

          // récupère la ville saisie
          val et = editText.text.toString()
          val service = retrofit().create(ActivitybyCity::class.java)
          runBlocking {
              // Récupère les données des items du layout
              val city = citydao?.getCity(et)
              if (city != null) {
                  city_id = city.id.toString()
              }else{
                  query = et
              }

              categories =
                  liste_cat_active(btn_museum, btn_food, btn_night, btn_fun, btn_other, btn_sport)

              //lancement de la requête api
              val result = service.listAct(query, "AUTO", "AUTO", "relevance-city", city_id, price_range, categories, "20", aller_date, retour_date, lang, monnaie)

              //Traitement du résultat
              if (result.meta.count != 0L) {
                  layoutNoActivities_frag.visibility = View.GONE
                  var list_activities_bdd = activityDaoSaved?.getActivity()
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
                  val activities = (activityDaoSearch?.getActivity())?.toMutableList()
                  activities_recyclerview.adapter =
                      ActivityAdapterGlobal(activities!!, list_favoris)
              } else {
                  layoutNoActivities_frag.visibility = View.VISIBLE
//                  val activities = null
//                  activities_recyclerview.adapter =
//                      ActivityAdapterGlobal(activities!!, list_favoris)
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
                    ActivityAdapterGlobal(activities.toMutableList(), list_favoris)
            }


        }
    }
    override fun onPause() {
        super.onPause()
        mBundleRecyclerViewState = Bundle()

        mListState = activities_recyclerview.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState!!.putParcelable("keyR", mListState)
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
