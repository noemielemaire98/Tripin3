package com.example.tripin.trip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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


        val root : View = inflater.inflate(R.layout.fragment_trip, container, false)
        var voyage_recyclerview = root.findViewById<View>(R.id.voyage_recyclerview) as RecyclerView
        voyage_recyclerview.layoutManager = LinearLayoutManager(this.context)

        val bt_search = root.findViewById<Button>(R.id.bt_recherche)
        val searchText = root.findViewById<AutoCompleteTextView>(R.id.search_voyage)

        val fab: FloatingActionButton = root.findViewById(R.id.fab_add)

        fab.setOnClickListener {
            val intent = Intent(this.context, AddVoyage::class.java)
            startActivity(intent)

        }
        val database =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "savedDatabase")
                .build()

        voyageDao = database.getVoyageDao()
        //voyage_recyclerview.adapter = VoyageAdapter(Voyage.all)



        runBlocking {
            val list_voyage = voyageDao?.getVoyage()
            list_voyage?.map {
                list_voyage_title.add(it.titre)
//                list_voyage_title.add(it.destination)

            }
        }

        Log.d("epf", "$list_voyage_title")
        val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,list_voyage_title)
        Log.d("epf","aaaaaa")


        searchText.setAdapter(adapter)

        bt_search.setOnClickListener {
            hideKeyboard()
                runBlocking {
                    val voyage = voyageDao?.getVoyageByTitre(search_voyage.text.toString())
                    if(voyage !=null){
                        voyages.add(voyage!!)
                        Log.d("epf",voyages.toString())
                        voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
                        voyage_recyclerview.layoutManager = LinearLayoutManager(requireContext())
                    }else{
                        Toast.makeText(
                            requireContext(),
                            "Voyage introuvable",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

        }

        return root
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val voyages  = voyageDao?.getVoyage()
            voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
//            val list_activities = listOf<Activity>()
//            val list_flights = listOf<Flight>()
//            val list_hotels = listOf<Hotel>()
//            val voyage =Voyage(0,"titre","debut","fin",R.drawable.destination1,0,list_activities, list_flights, list_hotels, destination, budget)
        }

    }


    override fun onPause() {
        super.onPause()
        mBundleRecyclerViewState = Bundle()

        mListState = voyage_recyclerview.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState!!.putParcelable("keyR", mListState)
    }


    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(android.app.Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}

