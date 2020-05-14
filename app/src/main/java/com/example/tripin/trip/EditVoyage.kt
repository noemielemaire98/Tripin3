package com.example.tripin.trip

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import androidx.room.Room
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.find.flight.IgnoreAccentsArrayAdapter
import com.example.tripin.model.Voyage
import com.example.tripin.trip.DetailVoyage
import kotlinx.android.synthetic.main.activity_edit_voyage.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class EditVoyage() : AppCompatActivity() {


    private var voyage: Voyage? = null
    private var id: Int = 0
    var titre: String = "a"
    var dateDepart: String = "000"
    var dateRetour: String = "000"
    var nbvoyageur: Int = 0
    private var voyageDao: VoyageDao? = null
    var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_voyage)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        id = intent.getIntExtra("id", 0)
        titre = intent.getStringExtra("titre")
        dateDepart = intent.getStringExtra("dateDepart")
        dateRetour = intent.getStringExtra("dateRetour")
        nbvoyageur = intent.getIntExtra("nbvoyager", 0)

        Log.d("EPF"," $id, $titre, $dateDepart, $dateRetour, $nbvoyageur")

        editv_titre_editText.hint = titre
        editv_dateDepart.hint = dateDepart
        editv_dateRetour.hint = dateRetour
        //editv_nbvoyageur_editText.hint = nbvoyageur.toString()


//        val database =
//            Room.databaseBuilder(this, AppDatabase::class.java, "gestionvoyages")
//                .build()
//
//        voyageDao = database.getVoyageDao()
//
//        var titre:String?= "zz"
//        var dateDepart:String?= "000"
//
//        runBlocking {
//            voyage = voyageDao!!.getVoyage(id) // Référence aux coroutines Kotlin
//            titre = voyage?.titre
//            var dateDepart = voyage?.date
//            var dateRetour = voyage?.dateRetour
//            var nb_voyageur = voyage?.nb_voyageur.toString()
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

        editv_passengers_number.hint = nbvoyageur.toString()
        var nbpasager = findViewById<AutoCompleteTextView>(R.id.editv_passengers_number)

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
                if (editv_titre_editText.text.isNotEmpty()) {
                    titre = editv_titre_editText.text.toString()
                }
                if (editv_dateDepart.text.toString().isNotEmpty()) {
                    dateDepart = editv_dateDepart.text.toString()
                }
                if (editv_dateRetour.text.toString().isNotEmpty()) {
                    dateRetour = editv_dateRetour.text.toString()
                }
                if(editv_passengers_number.text.toString().isNotEmpty()){
                    nbvoyageur = editv_passengers_number.text.toString().toInt()
                }




                // finish dépile l'activité et revient à la page d'en dessous

            Log.d("EPF"," $id, $titre, $dateDepart, $dateRetour, $nbvoyageur")
            val nvvoyage =
                Voyage(id, titre, dateDepart, dateRetour,
                    R.drawable.destination1, nbvoyageur)


                val database: AppDatabase =
                    Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase").build()
                val voyageDao: VoyageDao = database.getVoyageDao()

                runBlocking {
                    voyageDao.updateVoyage(nvvoyage)
                }
                finish()
//                val intent= Intent(it.context, DetailVoyage::class.java)
//                intent.putExtra("id",id)
//                it.context.startActivity(intent)
                    true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> onOptionsItemSelected(item)
        }
}

