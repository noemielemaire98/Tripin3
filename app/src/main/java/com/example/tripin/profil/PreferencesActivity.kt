package com.example.tripin.profil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.Room
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.PreferenceDao
import com.example.tripin.find.IgnoreAccentsArrayAdapter
import com.example.tripin.model.Preference
import kotlinx.android.synthetic.main.activity_preferences.*
import kotlinx.coroutines.runBlocking

class PreferencesActivity : AppCompatActivity() {

    private var preferenceDao: PreferenceDao? = null
    var ville = ""
    var destination = ""
    var budget = 500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val edit_ville = findViewById<EditText>(R.id.ville_edit_preferences)
        val edit_destination = findViewById<EditText>(R.id.destination_edit_preferences)
        val edit_budget = findViewById<EditText>(R.id.budget_edit_preferences)
        val bsave = findViewById<Button>(R.id.button_save)

//        Log.d("KLM", "${edit_budget.text.toString().toInt()}")

        //budget = edit_budget.text.toString().toInt()

        val destinationCat = findViewById<AutoCompleteTextView>(R.id.destination_categorie)
        val listCatDestination = resources.getStringArray(R.array.catDestination)
        val adapterListCatDestination = IgnoreAccentsArrayAdapter(this,android.R.layout.simple_list_item_1, listCatDestination)
        destinationCat.setAdapter(adapterListCatDestination)



        val database = Room.databaseBuilder(
            this.baseContext,
            AppDatabase::class.java,
            "allpreferences"
        ).build()

        preferenceDao = database.getPreferenceDao()

        bsave.setOnClickListener {
            ville = edit_ville.text.toString()
            destination = edit_destination.text.toString()
            //budget = edit_budget.text.toString().toInt()
            val destinationCatVal = destination_categorie.text
            Log.d("tyui", "$destinationCatVal")
            Log.d("KLM", "$ville")
            Log.d("KLM", "$destination")
            Log.d("KLM", "$budget")
            runBlocking {
                preferenceDao?.deletePreferences()
                val pref = Preference("", ville, destination, destinationCatVal.toString(),budget)
                preferenceDao?.addPreference(pref)
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }



    }

}
