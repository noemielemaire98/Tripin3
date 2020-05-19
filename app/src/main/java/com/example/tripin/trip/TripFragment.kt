package com.example.tripin.trip

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Activity
import com.example.tripin.model.Flight
import com.example.tripin.model.Hotel
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.fragment_trip.*
import kotlinx.coroutines.runBlocking
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.ArrayList

class TripFragment : Fragment() {

    private var voyageDao : VoyageDao? = null
    private var mBundleRecyclerViewState: Bundle? = null
    private var mListState: Parcelable? = null
    var list_voyage_title = arrayListOf<String>()
    var voyages = arrayListOf<Voyage>()

    private var destination = ""
    private var budget = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_trip, container, false)
        val bt_search = view.findViewById<Button>(R.id.bt_recherche)

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



        runBlocking {
            val list_voyage = voyageDao?.getVoyage()
            list_voyage?.map {
                list_voyage_title?.add(it.titre)
            }
        }
//        val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,list_voyage_title)
//        Log.d("epf",adapter.toString())
//        search_voyage.setAdapter(adapter)

        bt_search.setOnClickListener {
                runBlocking {
                    val voyage = voyageDao?.getVoyageByTitre(search_voyage.text.toString())
                    voyages.add(voyage!!)
                    voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
                }

        }

        return root
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val voyages  = voyageDao?.getVoyage()
            voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
            val list_activities = listOf<Activity>()
            val list_flights = listOf<Flight>()
            val list_hotels = listOf<Hotel>()
//            val voyage =Voyage(0,"titre","debut","fin",R.drawable.destination1,0,list_activities, list_flights, list_hotels, destination, budget)
        }

    }


    override fun onPause() {
        super.onPause()
        mBundleRecyclerViewState = Bundle()

        mListState = voyage_recyclerview.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState!!.putParcelable("keyR", mListState)
    }




}

