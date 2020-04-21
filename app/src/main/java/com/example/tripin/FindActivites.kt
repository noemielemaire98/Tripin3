package com.example.tripin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.model.Activity
import com.example.tripin.ui.find.ActivitybyCity
import com.example.tripin.ui.find.retrofit
import kotlinx.android.synthetic.main.activity_find_activites.*


import kotlinx.coroutines.runBlocking
import java.util.ArrayList

class FindActivites : AppCompatActivity() {

    private var activityDao : ActivityDao? = null
    val lang : String = "fr-FR"
    val monnaie :String = "EUR"
    val list_activity : MutableList<Activity> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_activites)

        activities_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "allactivity")
                .build()

        activityDao = database.getActivityDao()



        bt_recherche_activity.setOnClickListener {

            val query = search_activity_bar.query
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


                activities_recyclerview.adapter = ActivityAdapter(list_activity ?: emptyList())

            }
        }
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
