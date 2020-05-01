package com.example.tripin.trip

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

    var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_voyage)

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)




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

        addv_dateDepart_editText?.setOnClickListener {
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

    }

    private fun updatDatedepartInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        addv_dateDepart_editText!!.setText(sdf.format(cal.getTime()))
    }

    private fun updatDateretourInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        addv_dateRetour_editText!!.setText(sdf.format(cal.getTime()))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_voyage,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.ic_menu_add_voyage ->{
                val titre = addv_titre_editText.text
                val dateDepart = addv_dateDepart_editText.text
                val dateRetour = addv_dateRetour_editText.text
                val nombrevoyageur= addv_nbvoyageur_editText.text

                // finish dépile l'activité et revient à la page d'en dessous
                val voyage = Voyage(0,titre.toString(),dateDepart.toString(), dateRetour.toString() ,R.drawable.destination1, nombrevoyageur.toString().toInt())
                val database: AppDatabase =
                    Room.databaseBuilder(this, AppDatabase::class.java, "gestionvoyages").build()
                val voyageDao: VoyageDao = database.getVoyageDao()

                runBlocking {
                    voyageDao.addVoyage(voyage)// Reference aux co-routines Kotlin
                }
                finish()

                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> onOptionsItemSelected(item)
        }

}

