package com.example.tripin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.ActionBar
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.activity_add_voyage.*
import kotlinx.coroutines.runBlocking

class AddVoyage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_voyage)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        addv_button.setOnClickListener {
            val titre = addv_titre_editText.text
            val date = addv_date_editText.text


            Log.d("Custumers", "Bonjour ${titre} $date}")
            // finish dépile l'activité et revient à la page d'en dessous


            val voyage = Voyage(0, titre.toString(), date.toString(), R.drawable.destination1, 0)

            val database: AppDatabase =
                Room.databaseBuilder(this, AppDatabase::class.java, "gestionvoyages").build()
            val voyageDao: VoyageDao = database.getVoyageDao()

            runBlocking {
                voyageDao.addVoyage(voyage)// Reference aux co-routines Kotlin
            }
            finish()

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
