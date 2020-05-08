package com.example.tripin.find.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.CityDao
import com.example.tripin.data.retrofit
import com.example.tripin.model.Activity
import kotlinx.android.synthetic.main.activity_find_activites.*
import kotlinx.android.synthetic.main.activity_find_activites.activities_recyclerview
import kotlinx.android.synthetic.main.fragment_find_activity2.*
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class FindActivityFragment : Fragment() {

    private var activityDaoSearch : ActivityDao? = null
    private var activityDaoSaved : ActivityDao? = null
    private lateinit var citydao : CityDao
    val lang : String = "fr-FR"
    val monnaie :String = "EUR"
    var list_favoris  = arrayListOf<Boolean>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_find_activity2, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.activities_recyclerview)
        val bt = view.findViewById<Button>(R.id.bt_recherche_activity)
        val editText = view.findViewById<EditText>(R.id.search_activity_bar)

        rv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        val databasesearch =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "searchDatabase")
                .build()
        val databasesaved =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "savedDatabase")
                .build()


        activityDaoSearch = databasesearch.getActivityDao()
        activityDaoSaved = databasesaved.getActivityDao()
        citydao = databasesaved.getCityDao()



      bt.setOnClickListener {
          layoutNoActivities_frag.visibility = View.GONE

          runBlocking {
              activityDaoSearch?.deleteActivity()
              val list_activities_bdd = citydao?.getCity()
              Log.d("ccc","$list_activities_bdd")
          }
          list_favoris.clear()

            val name = editText.text.toString()

            runBlocking {
                val city = citydao?.getCity(name)
                Log.d("ccc","$city")
                if (city != null) {

                val service = retrofit().create(ActivitybyCity::class.java)
                //val result = service.listActivity("$query", lang, monnaie)
                val result = service.listActivitybyCity(city.id, lang, monnaie)
                if (result.meta.count == 0L) {
                    layoutNoActivities_frag.visibility = View.VISIBLE
                } else {
                    layoutNoActivities_frag.visibility = View.GONE
                }

                val list_activities_bdd = activityDaoSaved?.getActivity()
                // le map permet d'appeler la fonction sur chacun des éléments d'une collection (== boucle for)
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


                    val activity = Activity(
                        it.uuid,
                        it.title,
                        it.cover_image_url,
                        it.retail_price.formatted_iso_value,
                        it.operational_days,
                        it.about
                    )
                    activityDaoSearch?.addActivity(activity)

                }
                val activities = activityDaoSearch?.getActivity()
                activities_recyclerview.adapter =
                    ActivityAdapter(activities ?: emptyList(), list_favoris)
            }
                else {
                    layoutNoActivities_frag.visibility = View.VISIBLE
                }
            }
        }

        return view
    }

}
