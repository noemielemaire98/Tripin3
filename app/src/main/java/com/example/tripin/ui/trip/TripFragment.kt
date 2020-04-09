package com.example.tripin.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.VoyageAdapter
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.fragment_trip.*
import kotlinx.coroutines.runBlocking

class TripFragment : Fragment() {

    private lateinit var dashboardViewModel: TripViewModel
    private var voyageDao : VoyageDao? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(TripViewModel::class.java)
        val root : View = inflater.inflate(R.layout.fragment_trip, container, false)

        var voyage_recyclerview = root.findViewById<View>(R.id.voyage_recyclerview) as RecyclerView
        voyage_recyclerview.layoutManager = LinearLayoutManager(this.context)

        val database =
            Room.databaseBuilder(this.context!!, AppDatabase::class.java, "gestionvoyages")
                .build()

        voyageDao = database.getVoyageDao()
        //voyage_recyclerview.adapter = VoyageAdapter(Voyage.all)
        return root
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val clients  = voyageDao?.getVoyage()
            voyage_recyclerview.adapter = VoyageAdapter(clients ?: emptyList())
            val voyage =Voyage(0,"titre","date",R.drawable.destination1)
            voyageDao?.addVoyage(voyage)
        }
    }
}

