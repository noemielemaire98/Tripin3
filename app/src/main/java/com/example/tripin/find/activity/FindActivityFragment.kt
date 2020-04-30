package com.example.tripin.find.activity

import android.os.Bundle
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
import com.example.tripin.data.retrofit
import com.example.tripin.model.Activity
import kotlinx.android.synthetic.main.activity_find_activites.*
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class FindActivityFragment : Fragment() {

    private var activityDao : ActivityDao? = null
    val lang : String = "fr-FR"
    val monnaie :String = "EUR"
    val list_activity : MutableList<Activity> = mutableListOf()

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

        val database =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "allactivity")
                .build()

        activityDao = database.getActivityDao()


      bt.setOnClickListener {

            list_activity.clear()

            val query = editText.text.toString()
            runBlocking {
                val service = retrofit().create(ActivitybyCity::class.java)
                val result = service.listActivity("$query", lang, monnaie)
                val list_activities_bdd = activityDao?.getActivity()
                // le map permet d'appeler la fonction sur chacun des éléments d'une collection (== boucle for)
                result.data.map {
                    var favoris = false
                    val titre = it.title
                    list_activities_bdd?.forEach {
                        if(it.title == titre){
                            favoris = true

                        }
                    }

                    val activity = Activity(it.uuid, it.title, it.cover_image_url,it.retail_price.formatted_iso_value,it.operational_days,favoris,it.about)
                    //Log.d("CCC", "$activity")
                    list_activity.add(activity)
                    //Log.d("CIC","$list_activity")

                }


                activities_recyclerview.adapter =
                    ActivityAdapter(
                        list_activity ?: emptyList()
                    )

            }
        }









        return view
    }

}
