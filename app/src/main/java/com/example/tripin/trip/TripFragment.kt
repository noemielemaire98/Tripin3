package com.example.tripin.trip

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Activity
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.fragment_trip.*
import kotlinx.coroutines.runBlocking
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.ArrayList

class TripFragment : Fragment() {

    private var voyageDao : VoyageDao? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root : View = inflater.inflate(R.layout.fragment_trip, container, false)
        var voyage_recyclerview = root.findViewById<View>(R.id.voyage_recyclerview) as RecyclerView
        voyage_recyclerview.layoutManager = LinearLayoutManager(this.context)

        val fab: FloatingActionButton = root.findViewById(R.id.fab_add)

        fab.setOnClickListener { view ->
            val intent = Intent(this.context, AddVoyage::class.java)
            startActivity(intent)
            true
        }
        val database =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "savedDatabase")
                .build()

        voyageDao = database.getVoyageDao()
        //voyage_recyclerview.adapter = VoyageAdapter(Voyage.all)
        return root
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val voyages  = voyageDao?.getVoyage()
            voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
            val list_activities = listOf<Activity>()
            val voyage =Voyage(0,"titre","debut","fin",R.drawable.destination1,0,list_activities)

        }
    }

    //menu









}

