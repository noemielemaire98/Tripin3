package com.example.tripin.saved

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Activity
import com.example.tripin.model.Voyage
import com.example.tripin.trip.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_detail_voyage2.*
import kotlinx.coroutines.*

class DetailVoyageSave : AppCompatActivity() {

    var voyage: Voyage? = null
    private var id: Int = 0
    private var voyageDao: VoyageDao? = null
    lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    var listMarker2 = arrayListOf<Marker>()
    private var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_voyage2)
        voyage_map_layout.visibility = View.GONE
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_voyage) as SupportMapFragment

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        id = intent.getIntExtra("id", 0)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedVoyageDatabase")
                .build()

        voyageDao = database.getVoyageDao()
        runBlocking {
            voyage = voyageDao!!.getVoyage(id)
        }
        setupViewPager(viewpager_detail_voyage)
        viewpager_detail_voyage.offscreenPageLimit = 3
        tablayout_detail_voyage.setupWithViewPager(viewpager_detail_voyage)
        if (voyage?.photo != "") {
            Glide.with(this@DetailVoyageSave)
                .load(voyage?.photo)
                .centerCrop()
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.destination1)
        }


        tablayout_detail_voyage.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if ((tab!!.position == 0 || tab.position == 1 || tab.position == 2) || (tab.position == 3 && voyage!!.list_activity.isNullOrEmpty())) {
                    voyage_map_layout.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                } else {
                    voyage_map_layout.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                    setUpMapActivities(voyage!!.list_activity!!.toMutableList())
                }

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_voyage, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.ic_menu_delete_voyage -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.voyage_delete_confirm_title))
                    setMessage(getString(R.string.voyage_delete_confirm_message, voyage?.titre))
                    setNegativeButton(android.R.string.no) { _, _ ->

                    }
                    setPositiveButton(android.R.string.yes) { _, _ ->

                        runBlocking {
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

                intent.putExtra("id", voyage?.id)
                intent.putExtra("titre", voyage?.titre)
                intent.putExtra("dateDepart", voyage?.date)
                intent.putExtra("dateRetour", voyage?.dateRetour)
                intent.putExtra("nbvoyager", voyage?.nb_voyageur)
                intent.putExtra("destination", voyage?.destination)
                intent.putExtra("budget", voyage?.budget)

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
            val adapter =
                FindTabAdapterTrip(supportFragmentManager, voyage_map_layout, imageView, bundle)
            adapter.addFragment(InfoVoyageFrangment(), "Aperçu")
            adapter.addFragment(FlightTripFragment(), "Vol")
            adapter.addFragment(HotelTripFragment(), "Hotel")
            adapter.addFragment(ActivityTripFragment(), "Activites")
            withContext(Dispatchers.Main) {
                viewPager.adapter = adapter
            }
        }

    }

    private fun setUpMapActivities(activityList: MutableList<Activity>) {

        mapFragment.getMapAsync {
            map = it
            activityList.map { itA ->
                val marker: Marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(itA.latitude, itA.longitude))
                        .title(itA.title)
                )
                listMarker2.add(marker)
            }
            if (activityList.isNotEmpty()) {
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            activityList[0].latitude,
                            activityList[0].longitude
                        ), 10f
                    )
                )
            }

        }

    }

}





