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

class FindActivites : AppCompatActivity() {

    private var activityDao : ActivityDao? = null
    val lang : String = "fr-FR"
    val monnaie :String = "EUR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_activites)

        activities_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "allactivity")
                .build()
        runBlocking {
            activityDao?.deleteActivity()
        }

        bt_recherche_activity.setOnClickListener {

            val query = search_activity_bar.query
            runBlocking {
                activityDao?.deleteActivity()
                val service = retrofit().create(ActivitybyCity::class.java)
                val result = service.listActivity("$query", lang, monnaie)
                // le map permet d'appeler la fonction sur chacun des éléments d'une collection (== boucle for)
                result.data.map {

                    val activity = Activity(0, it.title, it.cover_image_url,it.retail_price.formatted_iso_value,it.operational_days,false,it.about)
                    Log.d("CCC", "$activity")
                    activityDao?.addActivity(activity)

                }
                activityDao = database.getActivityDao()
                val activities = activityDao?.getActivity()
                activities_recyclerview.adapter = ActivityAdapter(activities  ?: emptyList())
                Log.d("CCC","get")
            }

        }
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val activities  = activityDao?.getActivity()
            activities_recyclerview.adapter = ActivityAdapter(activities ?: emptyList())
        }
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
