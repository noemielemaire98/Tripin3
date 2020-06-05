package com.example.tripin.find.activity


import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.model.Activity
import com.example.tripin.data.retrofit
import kotlinx.android.synthetic.main.activity_find_activites.*
import kotlinx.android.synthetic.main.activity_find_activites.activities_recyclerview
import kotlinx.android.synthetic.main.activity_find_activites.bt_recherche_activity
import kotlinx.android.synthetic.main.activity_find_activites.search_activity_bar
import kotlinx.coroutines.runBlocking

class FindActivitesActivity : AppCompatActivity() {

    private var activityDaoSearch : ActivityDao? = null
    private var activityDaoSaved : ActivityDao? = null
    val lang : String = "fr-FR"
    val monnaie :String = "EUR"
    var list_favoris  = arrayListOf<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_activites)

        activities_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val databasesearch =
            Room.databaseBuilder(this, AppDatabase::class.java, "searchDatabase")
                .build()
        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()

        activityDaoSearch = databasesearch.getActivityDao()
        activityDaoSaved = databasesaved.getActivityDao()



        search_activity_bar.setOnClickListener { search_activity_bar.isIconified = false }


        bt_recherche_activity.setOnClickListener {
            hideKeyboard()
            search_activity_bar.clearFocus()

            runBlocking {
                activityDaoSearch?.deleteActivity()
            }
            list_favoris.clear()
            val query = search_activity_bar.query

            runBlocking {
                val service = retrofit().create(ActivitybyCity::class.java)
                val result = service.listActivity("$query", lang, monnaie)
                if (result.meta.count == 0L){
                    layoutNoActivities_activity.visibility = View.VISIBLE
                }else {
                    layoutNoActivities_activity.visibility = View.GONE
                }

                val list_activities_bdd = activityDaoSaved?.getActivity()
                // le map permet d'appeler la fonction sur chacun des éléments d'une collection (== boucle for)
                result.data.map {
                    val titre = it.title
                    var match_bdd = false
                    list_activities_bdd?.forEach {
                        if(it.title == titre){
                            list_favoris.add(true)
                            match_bdd = true
                        }
                    }
                    if (match_bdd == false ){list_favoris.add(false)}

                    var list_cat =  it.categories.map {
                        it.name
                    }


                    val activity = Activity(it.uuid, it.title,it.city.name, it.cover_image_url,it.retail_price.formatted_iso_value,it.operational_days,it.reviews_avg,list_cat,it.url,it.top_seller,it.must_see,it.description,it.about,it.latitude,it.longitude)
                    activityDaoSearch?.addActivity(activity)

                }
                val activities = activityDaoSearch?.getActivity()
                activities_recyclerview.adapter = ActivityAdapterGlobal(activities!!.toMutableList(),list_favoris)

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
