package com.example.tripin

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.room.Room
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.activity_add_voyage.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class AddVoyage : AppCompatActivity() {

    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_voyage)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fun onOptionsItemSelected(item: MenuItem)  =
            when (item.itemId) {
                R.id.home -> {
                    finish()
                    true
                }

                else -> super.onOptionsItemSelected(item)

            }


        val dateDepartSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updatDatedepartInView()
            }
        val dateRetourSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updatDateretourInView()
            }

        addv_date_editText?.setOnClickListener {
            var dialog = DatePickerDialog(
                this,
                dateDepartSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }

        addv_dateRetour_editText?.setOnClickListener {
            var dialog = DatePickerDialog(
                this,
                dateRetourSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }


        addv_valider.setOnClickListener {
            val titre = addv_titre_editText.text
            val date = addv_date_editText.text
            val dateRetour = addv_dateRetour_editText.text
            val nb_voyageur= addv_nbvoyageur_editText.text



            Log.d("Custumers","Bonjour ${titre} ${date}")
            // finish dépile l'activité et revient à la page d'en dessous


            val voyage = Voyage(0,titre.toString(),date.toString(), dateRetour.toString() ,R.drawable.destination1, 2)

            val database: AppDatabase =
                Room.databaseBuilder(this,AppDatabase::class.java,"gestionvoyages").build()
            val voyageDao :VoyageDao = database.getVoyageDao()

            runBlocking {
                voyageDao.addVoyage(voyage)// Reference aux co-routines Kotlin
            }
            finish()

        }

    }

    private fun updatDatedepartInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        addv_date_editText!!.setText(sdf.format(cal.getTime()))
    }

    private fun updatDateretourInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        addv_dateRetour_editText!!.setText(sdf.format(cal.getTime()))
    }


}
