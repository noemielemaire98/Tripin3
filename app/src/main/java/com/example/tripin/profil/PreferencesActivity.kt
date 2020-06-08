package com.example.tripin.profil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.Room
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.*
import com.example.tripin.find.IgnoreAccentsArrayAdapter
import com.example.tripin.model.Preference
import kotlinx.android.synthetic.main.activity_preferences.*
import kotlinx.coroutines.runBlocking

class PreferencesActivity : AppCompatActivity() {

    private var preferenceDao: PreferenceDao? = null
    private var hotelDao: HotelDao? = null
    private var activityDao: ActivityDao? = null
    var ville = ""
    var destination = ""
    var budget = 100
    private lateinit var citydao: CityDao
    var list_cities_name = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        //val edit_ville = findViewById<EditText>(R.id.ville_edit_preferences)
        val edit_destination = findViewById<EditText>(R.id.destination_edit_preferences)
        val edit_budget = findViewById<EditText>(R.id.budget_edit_preferences)
        val bsave = findViewById<Button>(R.id.button_save)

//        Log.d("KLM", "${edit_budget.text.toString().toInt()}")

        //budget = edit_budget.text.toString().toInt()

        val destinationCat = findViewById<AutoCompleteTextView>(R.id.destination_categorie)
        val listCatDestination = resources.getStringArray(R.array.catDestination)
        val adapterListCatDestination =
            IgnoreAccentsArrayAdapter(this, android.R.layout.simple_list_item_1, listCatDestination)
        destinationCat.setAdapter(adapterListCatDestination)

        val activityCat = findViewById<AutoCompleteTextView>(R.id.activity_categorie)
        val listCatActivity = resources.getStringArray(R.array.catActivity)
        val adapterListCatActivity =
            IgnoreAccentsArrayAdapter(this, android.R.layout.simple_list_item_1, listCatActivity)
        activityCat.setAdapter(adapterListCatActivity)


        val database = Room.databaseBuilder(
            this.baseContext,
            AppDatabase::class.java,
            "allpreferences"
        ).build()

        val databaseHome = Room.databaseBuilder(
            this.baseContext,
            AppDatabase::class.java,
            "homeDatabase"
        ).build()

        val databasesaved =
            Room.databaseBuilder(this.baseContext, AppDatabase::class.java, "savedDatabase")
                .build()

        preferenceDao = database.getPreferenceDao()
        hotelDao = databaseHome.getHotelDao()
        activityDao = databaseHome.getActivityDao()


        // récupération des villes possibles
        citydao = databasesaved.getCityDao()

        // ajout dans l'auto complétion texte
        runBlocking {
            val list_cities_bdd = citydao.getCity()
            list_cities_bdd.map {
                list_cities_name.add(it.name!!)
            }
        }
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, list_cities_name)

        clearFocusAutoTextView(destination_edit_preferences)

        destination_edit_preferences.setAdapter(adapter)

        bsave.setOnClickListener {
            //ville = edit_ville.text.toString()
            destination = edit_destination.text.toString()
            budget = edit_budget.text.toString().toInt()
            val destinationCatVal = destination_categorie.text
            val activityCatVal = activity_categorie.text
            Log.d("KLM", "choix envie : $destinationCatVal")
            Log.d("KLM", "destination $destination")
            Log.d("KLM", "budget $budget")
            if (destination == "" || destinationCatVal.toString() == "" || activityCatVal.toString() == "" || edit_budget.text.toString() == "" || budget == null) {
                Toast.makeText(this, "Veuillez compléter tous les champs", Toast.LENGTH_SHORT)
                    .show()
            } else {
                runBlocking {
                    preferenceDao?.deletePreferences()

                    hotelDao?.deleteHotels()
                    activityDao?.deleteActivity()

                    val pref = Preference(
                        "",
                        destination,
                        destinationCatVal.toString(),
                        budget,
                        activityCatVal.toString()
                    )
                    preferenceDao?.addPreference(pref)
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
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

    // vide textView si non choisi dans la liste
    private fun clearFocusAutoTextView(autoCompleteTextView: AutoCompleteTextView) {
        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (!b) {
                // on focus off
                val str: String = autoCompleteTextView.text.toString()
                val listAdapter: ListAdapter = autoCompleteTextView.adapter
                for (i in 0 until listAdapter.count) {
                    val temp: String = listAdapter.getItem(i).toString()
                    if (str.compareTo(temp) == 0) {
                        return@OnFocusChangeListener
                    }
                }
                autoCompleteTextView.setText("")
            }
        }
    }

}
