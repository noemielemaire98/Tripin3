package com.example.tripin.trip

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.find.activity.ActivityAdapterTrip
import com.example.tripin.model.Activity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tripin.model.Voyage
import com.example.tripin.saved.DetailVoyageSave
import kotlinx.coroutines.runBlocking


class ActivityTripFragment : Fragment() {

    private var list_fav = arrayListOf<Boolean>()
    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragment = arguments?.getString("fragment")

        val voyage: Voyage?
        val listMarker2: ArrayList<Marker>

        if(fragment == "DetailVoyage2") {

            voyage = (activity as? DetailVoyage2)!!.voyage
            listMarker2 = (activity as? DetailVoyage2)!!.listMarker2

        } else {
            voyage = (activity as? DetailVoyageSave)!!.voyage
            listMarker2 = (activity as? DetailVoyageSave)!!.listMarker2
        }


        val view = inflater.inflate(R.layout.activity_tripdetails_activites, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.activitiessaved_recyclerview)
        val iv = view.findViewById<ImageView>(R.id.noActivityImage)
        val rl = view.findViewById<RelativeLayout>(R.id.layout_nosavedActivities)

        rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        iv.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("switchView", 3)
            startActivity(intent)
            activity?.finish()
        }

       // mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        runBlocking {
            val activities = voyage!!.list_activity
            activities?.map {
                list_fav.add(true)
            }
            if (activities!!.isEmpty()) {
                rl.visibility = View.VISIBLE
            } else {
                rl.visibility = View.GONE
                val activities2 = activities.toMutableList()
               // val listMarker = setUpMap(activities2)
                rv.adapter = ActivityAdapterTrip(activities2, list_fav, voyage,listMarker2,rl)
            }
        }







        return view
    }


    private fun setUpMap(activityList : MutableList<Activity>) : ArrayList<Marker>{
        var listMarker = arrayListOf<Marker>()

        mapFragment.getMapAsync {
            map = it
            activityList.map {
                val marker : Marker = map.addMarker(MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .title(it.title))
                listMarker.add(marker)
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(activityList[0].latitude,activityList[0].longitude), 10f))
        }

        return listMarker
    }

}
