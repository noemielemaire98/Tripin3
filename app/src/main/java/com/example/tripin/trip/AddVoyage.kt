package com.example.tripin.trip

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.CityDao
import com.example.tripin.data.VoyageDao
import com.example.tripin.find.flight.IgnoreAccentsArrayAdapter
import com.example.tripin.model.*
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_add_voyage.*
import kotlinx.android.synthetic.main.activity_add_voyage.passengers_number
import kotlinx.android.synthetic.main.activity_find_flight.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.colorAttr
import java.text.SimpleDateFormat
import java.util.*

class AddVoyage : AppCompatActivity() {

    var cal: Calendar = Calendar.getInstance()
    private lateinit var citydao: CityDao
    private var voyageDao : VoyageDao? = null
    var list_cities_name = arrayListOf<String>()
    var budget= "100"
    var image =""
    var city :City? = null

    private lateinit var context: Context


    @RequiresApi(Build.VERSION_CODES.M)
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

        //selection de la ville
        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()


        citydao = database.getCityDao()

        runBlocking {
            val list_cities_bdd = citydao?.getCity()
            list_cities_bdd.map {
                list_cities_name.add(it.name!!)
            }
        }
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, list_cities_name)
        addv_destination.setAdapter(adapter)


        //liste nombre de participant
        var nbpasager = findViewById<AutoCompleteTextView>(R.id.passengers_number)

        val passengersNumber = resources.getStringArray(R.array.passengersNumber)
        val adapterPassengers =
            IgnoreAccentsArrayAdapter(this, android.R.layout.simple_list_item_1, passengersNumber)

        nbpasager.setAdapter(adapterPassengers)

        var seekbar =  findViewById<SeekBar>(R.id.addv_budget)



        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbarView: SeekBar, progress: Int, fromUser: Boolean) {
                budget = seekbarView.progress.toString()
                addv_valeur_budget.text = "Budget : $budget €"
            }

            override fun onStartTrackingTouch(seekbarView: SeekBar) {
                 //Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
            }
        })


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
                val database =
                    Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                        .build()

                voyageDao = database.getVoyageDao()
                citydao = database.getCityDao()


                Log.d("oct", addv_titre_editText.text.toString())


                runBlocking {
                    val voyages = voyageDao?.getVoyageByTitre(addv_titre_editText.text.toString())
                    city = citydao.getCity(addv_destination.text.toString())

                    Log.d("oct", "city =$city")

                    if (addv_titre_editText.text.isEmpty() || addv_dateDepart.text.toString()
                            .isEmpty() || addv_dateRetour.text.toString()
                            .isEmpty() || addv_destination.text.toString().isEmpty()
                    ) {
                        Toast.makeText(
                            this@AddVoyage,
                            "Tous les champs ne sont pas remplis",
                            Toast.LENGTH_SHORT
                        ).show()


                    } else if (voyages != null ) {
                        Toast.makeText(
                            this@AddVoyage,
                            "Titre du voyage deja utilisé",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if(city == null){
                        addv_destination.hint =  addv_destination.text
                        addv_destination.text = null
                        Toast.makeText(
                            this@AddVoyage,
                            "Destination indiponible",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else {
                        val titre = addv_titre_editText.text
                        val dateDepart = addv_dateDepart.text
                        val dateRetour = addv_dateRetour.text
                        val nombrevoyageur = passengers_number.text
                        val destination = addv_destination.text
                        val budget = budget

                        val citie = citydao.getCity(destination.toString())
                        image = citie.cover_image_url.toString()


                        val list_activities = listOf<Activity>()
                        val list_flights = listOf<Flight>()
                        val list_hotels = listOf<Hotel>()

                        val voyage = Voyage(
                            0,
                            titre.toString(),
                            dateDepart.toString(),
                            dateRetour.toString(),
                            image,
//                            R.drawable.destination1,
                            nombrevoyageur.toString().toInt(),
                            list_activities,
                            list_flights,
                            list_hotels,
                            destination.toString(),
                            budget

                        )

//                        val database: AppDatabase =
//                            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
//                                .build()
                        val voyageDao: VoyageDao = database.getVoyageDao()

                        runBlocking {
                            voyageDao.addVoyage(voyage)// Reference aux co-routines Kotlin
                        }

//

                        finish()
                    }
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

