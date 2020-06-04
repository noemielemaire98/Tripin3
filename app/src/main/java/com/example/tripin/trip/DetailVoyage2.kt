package com.example.tripin.trip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.find.voyage.FindVoyage
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.activity_detail_voyage2.*
import kotlinx.coroutines.*

class DetailVoyage2 : AppCompatActivity() {

    var voyage: Voyage?=null
    private var id : Int=0
    private var voyageDao : VoyageDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_voyage2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        id = intent.getIntExtra("id",0)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()

        voyageDao = database.getVoyageDao()
        runBlocking {
            voyage = voyageDao!!.getVoyage(id)
        }
        setupViewPager(viewpager_detail_voyage)
        viewpager_detail_voyage.offscreenPageLimit = 3
        tablayout_detail_voyage.setupWithViewPager(viewpager_detail_voyage)
        val url = voyage?.photo
        Glide.with(this@DetailVoyage2)
            .load(url)
            .centerCrop()
            .into(imageView)

        if(viewpager_detail_voyage.currentItem != 0 ){
            imageView.setImageResource(R.drawable.destination1)
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
                intent.putExtra("destination",voyage?.destination)
                intent.putExtra("budget",voyage?.budget)

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
    private fun setupViewPager(viewPager: ViewPager) {
        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        scope.launch {
            val adapter = FindTabAdapterTrip(supportFragmentManager)
            adapter.addFragment(InfoVoyageFrangment(), "Aperçu")
//            adapter.addFragment(FindVoyage(), "Aperçu")
            adapter.addFragment(FlightTripFragment(), "Vol")
            adapter.addFragment(HotelTripFragment(), "Hotel")
            adapter.addFragment(ActivityTripFragment(), "Activites")
            withContext(Dispatchers.Main) {
                viewPager.adapter = adapter
            }
        }

    }




}
