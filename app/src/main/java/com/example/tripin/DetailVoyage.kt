package com.example.tripin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
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

        id = intent.getIntExtra("id",0)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "gestionvoyages")
                .build()

        voyageDao = database.getVoyageDao()
        runBlocking {
            voyage = voyageDao!!.getVoyage(id) // Référence aux coroutines Kotlin
            voyage_title_textview.text = voyage?.titre
            voyage_date_textview.text = voyage?.date
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_voyage,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem)=

        when (item.itemId) {
            R.id.ic_menu_delete_voyage -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.voyage_delete_confirm_title))
                    setMessage(getString(R.string.voyage_delete_confirm_message,voyage?.titre))
                    setNegativeButton(android.R.string.no){_,_ ->
                        Log.d("gege","non supprimé")
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
            else -> true

        }


}
