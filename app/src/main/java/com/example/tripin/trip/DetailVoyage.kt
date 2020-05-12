package com.example.tripin.trip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.activity_detail_voyage.*
import kotlinx.coroutines.runBlocking

class DetailVoyage : AppCompatActivity() {

    private var voyage: Voyage?=null
    private var id : Int=0
    private var voyageDao : VoyageDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_voyage)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra("id",0)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()

        voyageDao = database.getVoyageDao()
        runBlocking {
            voyage = voyageDao!!.getVoyage(id) // Référence aux coroutines Kotlin
            voyage_title_textview.text = "Voyage : ${voyage?.titre}"
            voyage_dateDepart_textview.text = "Du "+voyage?.date
            voyage_dateRetour_textview.text = "Au "+voyage?.dateRetour
            voyage_nb_voyageurs_textview.text = "Nombre de voyageur :" +voyage?.nb_voyageur
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_voyage,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean=

        when (item.itemId) {
            R.id.ic_menu_delete_voyage -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.voyage_delete_confirm_title))
                    setMessage(getString(R.string.voyage_delete_confirm_message,voyage?.titre))
                    setNegativeButton(android.R.string.no){_,_ ->

                    }
                    setPositiveButton(android.R.string.yes){_,_ ->

                        runBlocking(){
                            voyageDao?.deleteVoyage(voyage!!)
                        }

                        finish()
                    }
                    show()

                }
                true
            }
            R.id.ic_menu_edit_voyage -> {
                val intent = Intent(this, EditVoyage::class.java)
                intent.putExtra("id",voyage?.id)
                intent.putExtra("titre",voyage?.titre)
                intent.putExtra("dateDepart",voyage?.date)
                intent.putExtra("dateRetour",voyage?.dateRetour)
                intent.putExtra("nbvoyager",voyage?.nb_voyageur)

                startActivity(intent)
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
