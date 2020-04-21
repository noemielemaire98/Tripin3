package com.example.tripin


import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
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


        search_activity_bar.setOnClickListener { search_activity_bar.isIconified = false }


        bt_recherche_activity.setOnClickListener {
            hideKeyboard()
            search_activity_bar.clearFocus()
            list_activity.clear()

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

    private fun android.app.Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(android.app.Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
