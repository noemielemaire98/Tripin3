package com.example.tripin.trip

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.SeekBar
import android.widget.Toast
import androidx.room.Room
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.CityDao
import com.example.tripin.data.VoyageDao
import com.example.tripin.find.flight.IgnoreAccentsArrayAdapter
import com.example.tripin.model.*
import com.example.tripin.trip.DetailVoyage
import kotlinx.android.synthetic.main.activity_edit_voyage.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class EditVoyage() : AppCompatActivity() {


    private lateinit var citydao: CityDao
    var list_cities_name = arrayListOf<String>()
    private var voyage1: Voyage ?= null
    private var id: Int = 0
    var titre: String = "a"
    var dateDepart: String ?= "000"
    var dateRetour: String ?= "000"
    var nbvoyageur: Int ?= 0
    var destination = ""
    var budget: String = "1000"
    var city : City? = null

    private var voyageDao: VoyageDao? = null
    var cal: Calendar = Calendar.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_voyage)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        id = intent.getIntExtra("id", 0)
        titre = intent.getStringExtra("titre")
        dateDepart = intent.getStringExtra("dateDepart")
        dateRetour = intent.getStringExtra("dateRetour")
        nbvoyageur = intent.getIntExtra("nbvoyager", 0)
        destination = intent.getStringExtra("destination")
        budget = intent.getStringExtra("budget")

        val databasevoyage =
        Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
            .build()
        voyageDao = databasevoyage.getVoyageDao()
        runBlocking {
            voyage1 = voyageDao!!.getVoyage(id)
        }
        Log.d("EPF"," $id, $titre, $dateDepart, $dateRetour, $nbvoyageur")

        editv_titre_editText.hint = titre
        editv_dateDepart.text = SpannableStringBuilder(dateDepart)
        editv_dateRetour.text = SpannableStringBuilder(dateRetour)
        editv_destination.hint = destination

        editv_budget.progress = budget.toInt()
        editv_valeur_budget.text = "Budget : $budget "

        //editv_nbvoyageur_editText.hint = nbvoyageur.toString()

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()

//        voyageDao = database.getVoyageDao()

//        var titre:String?= "zz"
//        var dateDepart:String?= "000"

//        runBlocking {
//            voyage = voyageDao!!.getVoyage(id) // Référence aux coroutines Kotlin
//            titre = voyage.titre
//            var dateDepart = voyage.date
//            var dateRetour = voyage.dateRetour
//            var nb_voyageur = voyage.nb_voyageur.toString()
//            var destination = voyage.destination
//
//            Log.d("EPF", "ancien voyage:$titre , $dateDepart, $dateRetour, $nb_voyageur")
//        }

        // Affiche le calendrier
        editv_dateDepart.setOnClickListener {

            rangeDatePickerPrimeCalendar()
        }
        // Affiche le calendrier pour choisir la date de retour
        editv_dateRetour.setOnClickListener {
            rangeDatePickerPrimeCalendar()
        }

        //selection de la ville

        citydao = database.getCityDao()

        runBlocking {
            val list_cities_bdd = citydao.getCity()
            list_cities_bdd.map {
                list_cities_name.add(it.name!!)
            }
        }
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, list_cities_name)
        editv_destination.setAdapter(adapter)


        editv_passengers_number.hint = nbvoyageur.toString()
        var nbpasager = findViewById<AutoCompleteTextView>(R.id.editv_passengers_number)

        val passengersNumber = resources.getStringArray(R.array.passengersNumber)
        val adapterPassengers =
            IgnoreAccentsArrayAdapter(this, android.R.layout.simple_list_item_1, passengersNumber)

        nbpasager.setAdapter(adapterPassengers)

        var seekbar =  findViewById<SeekBar>(R.id.editv_budget)

        seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbarView: SeekBar, progress: Int, fromUser: Boolean) {
                budget = seekbarView.progress.toString()
                editv_valeur_budget.text = "Budget : $budget €"
            }

            override fun onStartTrackingTouch(seekbarView: SeekBar) {
                // Write code to perform some action when touch is started.
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
            editv_dateDepart.setText(parsedStartDate)
            editv_dateRetour.setText(parsedEndDate)
        }

        val today = CivilCalendar()

        if (editv_dateDepart.text.toString() != "" && editv_dateRetour.text.toString() != "") {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calStart = CivilCalendar()
            calStart.timeInMillis = df.parse(editv_dateDepart.text.toString())!!.time
            val calEnd = CivilCalendar()
            calEnd.timeInMillis = df.parse(editv_dateRetour.text.toString())!!.time

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
        menuInflater.inflate(R.menu.menu_edit_voyage,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.ic_menu_modifier_voyage ->{

                val database =
                    Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                        .build()

                voyageDao = database.getVoyageDao()
                Log.d("oct", editv_titre_editText.text.toString())
                runBlocking {

                    val voyages = voyageDao?.getVoyageByTitre(editv_titre_editText.text.toString())
                    if(editv_destination.text.isNotEmpty()){
                        city = citydao.getCity(editv_destination.text.toString())
                    }else{
                        city = citydao.getCity(editv_destination.hint.toString())
                    }


                    Log.d("oct", "city =$city")

                    if (voyages != null) {
                        Toast.makeText(
                            this@EditVoyage,
                            "Titre du voyage deja utilisé",
                            Toast.LENGTH_SHORT
                        ).show()

                    }else if(city == null){
                        editv_destination.text = null
                        Toast.makeText(
                            this@EditVoyage,
                            "Destination indiponible",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else {


                        Log.d("oct", "update")

                        if (editv_titre_editText.text.isNotEmpty()) {
                            titre = editv_titre_editText.text.toString()
                        }
                        if (editv_dateDepart.text.toString().isNotEmpty()) {
                            dateDepart = editv_dateDepart.text.toString()
                        }
                        if (editv_dateRetour.text.toString().isNotEmpty()) {
                            dateRetour = editv_dateRetour.text.toString()
                        }
                        if (editv_passengers_number.text.toString().isNotEmpty()) {
                            nbvoyageur = editv_passengers_number.text.toString().toInt()
                        }
                        if (editv_destination.text.toString().isNotEmpty()) {
                            destination = editv_destination.text.toString()
                        }



                        // finish dépile l'activité et revient à la page d'en dessous

                        Log.d("EPF", " $id, $titre, $dateDepart, $dateRetour, $nbvoyageur")
                        val list_activities = voyage1?.list_activity
                        val list_flights = voyage1?.list_flights
                        val list_hotels = voyage1?.list_hotels
                        val citie = citydao.getCity(destination.toString())
                        var image = citie.cover_image_url

                        val nvvoyage =
                            Voyage(
                                id,
                                titre,
                                dateDepart,
                                dateRetour,
                                image,
                                nbvoyageur,
                                list_activities,
                                list_flights,
                                list_hotels,
                                destination,
                                budget
                            )

                        val voyageDao: VoyageDao = database.getVoyageDao()

                        runBlocking {
                            voyageDao.updateVoyage(nvvoyage)
                        }
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

