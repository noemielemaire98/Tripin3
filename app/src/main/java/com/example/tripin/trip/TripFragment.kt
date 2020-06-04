package com.example.tripin.trip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Voyage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_trip.*
import kotlinx.coroutines.runBlocking
import java.util.*

class TripFragment : Fragment() {

    private var voyageDao: VoyageDao? = null
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


        val root: View = inflater.inflate(R.layout.fragment_trip, container, false)
        var voyage_recyclerview = root.findViewById<View>(R.id.voyage_recyclerview) as RecyclerView
        voyage_recyclerview.layoutManager = LinearLayoutManager(this.context)

        val bt_search = root.findViewById<Button>(R.id.bt_recherche)
        val searchText = root.findViewById<AutoCompleteTextView>(R.id.search_voyage)
        val fab: FloatingActionButton = root.findViewById(R.id.fab_add)
//        val searchText2 = root.findViewById<SearchView>(R.id.searchtext)



        fab.setOnClickListener {
            val intent = Intent(this.context, AddVoyage::class.java)
            startActivity(intent)

        }
        val database =
            Room.databaseBuilder(
                requireActivity().baseContext,
                AppDatabase::class.java,
                "savedDatabase"
            )
                .build()

        voyageDao = database.getVoyageDao()
        //voyage_recyclerview.adapter = VoyageAdapter(Voyage.all)

        var list_voyage : List<Voyage> ?

        runBlocking {
            list_voyage = voyageDao?.getVoyage()
            list_voyage?.map {
                list_voyage_title.add(it.titre)
//                list_voyage_title.add(it.destination)

            }
        }



//         resultat.addAll(list)
//
//                   resultat.a
//
//        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText!!.isNotEmpty()){
//                    val search = newText.toLowerCase(Locale.getDefault() )
//                }
//                return true
//            }
//
//        })

        Log.d("epf", "$list_voyage_title")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list_voyage_title)
        Log.d("epf", "aaaaaa")


        searchText.setAdapter(adapter)


        bt_search.setOnClickListener {
            voyages.clear()
            hideKeyboard()
            if (search_voyage.text.isNotEmpty()){
                runBlocking {
                    val voyage = voyageDao?.getVoyageByTitre(search_voyage.text.toString())
                    if (voyage != null) {
                        voyages.add(voyage)
                        voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
                        voyage_recyclerview.layoutManager =
                            LinearLayoutManager(requireContext())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Voyage introuvable",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else if(search_voyage.text.isEmpty()){
                runBlocking {
                    val voyages = voyageDao!!.getVoyage()
                    voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
                }

            }
            search_voyage.text = null

        }

//        searchText2.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter.filter.filter(newText)
//                return false
//            }
//
//        })
        return root
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val voyages = voyageDao?.getVoyage()
            voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
//            val list_activities = listOf<Activity>()
//            val list_flights = listOf<Flight>()
//            val list_hotels = listOf<Hotel>()
//            val voyage =Voyage(0,"titre","debut","fin",R.drawable.destination1,0,list_activities, list_flights, list_hotels, destination, budget)
        }
//        runBlocking {
//            val voyage = voyageDao?.getVoyageByTitre(search_voyage.text.toString())
//            if (voyage != null) {
//                voyages.add(voyage)
//                voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
//                voyage_recyclerview.layoutManager =
//                    LinearLayoutManager(requireContext())
//            }
//        }

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


//    override fun onCreate(savedInstanceState: Bundle?) {
//        setHasOptionsMenu(true)
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
//        inflater.inflate(R.menu.menu_tripfragment, menu)
//        super.onCreateOptionsMenu(menu, inflater)
////        menuInflater.inflate(R.menu.menu_tripfragment, menu)
//    }


}

