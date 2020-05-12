package com.example.tripin.trip

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.find.flight.IgnoreAccentsArrayAdapter
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.activity_add_voyage.*
import kotlinx.android.synthetic.main.activity_add_voyage.passengers_number
import kotlinx.android.synthetic.main.activity_find_flight.*
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


        // Affiche le calendrier
        addv_dateDepart.setOnClickListener {

                rangeDatePickerPrimeCalendar()
        }
        // Affiche le calendrier pour choisir la date de retour
        addv_dateRetour.setOnClickListener {
            rangeDatePickerPrimeCalendar()
        }

        //liste nombre de participant
        var nbpasager = findViewById<AutoCompleteTextView>(R.id.passengers_number)

        val passengersNumber = resources.getStringArray(R.array.passengersNumber)
        val adapterPassengers =
            IgnoreAccentsArrayAdapter(this, android.R.layout.simple_list_item_1, passengersNumber)

        nbpasager.setAdapter(adapterPassengers)


    }


    // affichage du calendrier aller-retour
    private fun rangeDatePickerPrimeCalendar() {
        val rangeDaysPickCallback = RangeDaysPickCallback { startDate, endDate ->
            // TODO
            Log.d("Date", "${startDate.shortDateString} ${endDate.shortDateString}")
            val parser =
                SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val formatterDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedStartDate =
                formatterDate.format(parser.parse(startDate.shortDateString)!!)
            val parsedEndDate =
                formatterDate.format(parser.parse(endDate.shortDateString)!!)
            addv_dateDepart.setText(parsedStartDate)
            addv_dateRetour.setText(parsedEndDate)
        }

        val today = CivilCalendar()

        if (addv_dateDepart.text.toString() != "" && addv_dateRetour.text.toString() != "") {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calStart = CivilCalendar()
            calStart.timeInMillis = df.parse(addv_dateDepart.text.toString())!!.time
            val calEnd = CivilCalendar()
            calEnd.timeInMillis = df.parse(addv_dateRetour.text.toString())!!.time

            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .initiallyPickedRangeDays(calStart, calEnd)
                .firstDayOfWeek(Calendar.MONDAY)
                .minPossibleDate(today)
                .build()

            datePickerT.show(supportFragmentManager, "PrimeDatePickerBottomSheet")
        } else {
            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .firstDayOfWeek(Calendar.MONDAY)
                .minPossibleDate(today)
                .build()

            datePickerT.show(supportFragmentManager, "PrimeDatePickerBottomSheet")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_voyage, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.ic_menu_add_voyage -> {
                if (addv_titre_editText.text.isEmpty() || addv_dateDepart.text.toString()
                        .isEmpty() || addv_dateRetour.text.toString().isEmpty()
                ) {
                    Toast.makeText(
                        this@AddVoyage,
                        "Tous les champs ne sont pas rempli",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    val titre = addv_titre_editText.text
                    val dateDepart = addv_dateDepart.text
                    val dateRetour = addv_dateRetour.text
                    val nombrevoyageur = passengers_number.text

                    // finish dépile l'activité et revient à la page d'en dessous
                    val voyage = Voyage(
                        0,
                        titre.toString(),
                        dateDepart.toString(),
                        dateRetour.toString(),
                        R.drawable.destination1,
                        nombrevoyageur.toString().toInt()
                    )
                    val database: AppDatabase =
                        Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                            .build()
                    val voyageDao: VoyageDao = database.getVoyageDao()

                    runBlocking {
                        voyageDao.addVoyage(voyage)// Reference aux co-routines Kotlin
                    }
                    finish()
                }

                true

            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> onOptionsItemSelected(item)
        }


}

