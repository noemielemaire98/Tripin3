package com.example.tripin.trip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.find.voyage.FindVoyage
import com.example.tripin.model.Activity
import com.example.tripin.model.Hotel
import com.example.tripin.model.Voyage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_detail_voyage2.*
import kotlinx.coroutines.*

class DetailVoyage2 : AppCompatActivity() {

    var voyage: Voyage?=null
    private var id : Int=0
    private var voyageDao : VoyageDao? = null
    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    var listMarker2 = arrayListOf<Marker>()
    private var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_voyage2)
        voyage_map_layout.visibility = View.GONE
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_voyage) as SupportMapFragment

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        id = intent.getIntExtra("id",0)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()

        voyageDao = database.getVoyageDao()
        runBlocking {
            voyage = voyageDao!!.getVoyage(id)
        }
        bundle.putString("fragment", "DetailVoyage2")
        setupViewPager(viewpager_detail_voyage)
        viewpager_detail_voyage.offscreenPageLimit = 3
        tablayout_detail_voyage.setupWithViewPager(viewpager_detail_voyage)
        if(voyage?.photo != ""){
            Glide.with(this@DetailVoyage2)
                .load(voyage?.photo)
                .centerCrop()
                .into(imageView)
        }else{
            imageView.setImageResource(R.drawable.destination1)
        }


        tablayout_detail_voyage.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {

                if(tab!!.position == 3 && !voyage!!.list_activity.isNullOrEmpty()){
                    voyage_map_layout.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                    setUpMapActivities(voyage!!.list_activity!!.toMutableList())

                } else if ((tab!!.position == 2 && !voyage!!.list_hotels.isNullOrEmpty())){
                    voyage_map_layout.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                    setUpMapHotels(voyage!!.list_hotels!!.toMutableList())
                }
                else{
                    voyage_map_layout.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                }

        }

        })
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
            val adapter = FindTabAdapterTrip(supportFragmentManager,voyage_map_layout,imageView, bundle)
            adapter.addFragment(InfoVoyageFrangment(), "Aperçu")
            adapter.addFragment(FlightTripFragment(), "Vol")
            adapter.addFragment(HotelTripFragment(), "Hotel")
            adapter.addFragment(ActivityTripFragment(), "Activites")
            withContext(Dispatchers.Main) {
                viewPager.adapter = adapter
            }
        }

    }

    private fun setUpMapActivities(activityList : MutableList<Activity>) {

        mapFragment.getMapAsync {
            map = it
            it.clear()
            activityList.map {
                val marker: Marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(it.latitude, it.longitude))
                        .title(it.title)
                )
                listMarker2.add(marker)
            }
            if (!activityList.isEmpty()) {
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

        private fun setUpMapHotels(hotelList : MutableList<Hotel>){

            mapFragment.getMapAsync {
                map = it
                it.clear()
                hotelList.map {
                    val marker : Marker = map.addMarker(
                        MarkerOptions()
                            .position(LatLng(it.latitude, it.longitude))
                            .title(it.hotelName))
                    listMarker2.add(marker)
                }
                if(!hotelList.isEmpty()){
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(hotelList[0].latitude,hotelList[0].longitude), 10f))
                }

            }

    }




}
