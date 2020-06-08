package com.example.tripin.trip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Voyage
import com.example.tripin.saved.VoyagesDiffUtilCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trip.*
import kotlinx.coroutines.runBlocking
import java.text.Normalizer
import java.util.concurrent.TimeUnit

class TripFragment : Fragment() {

    private val regexUnaccent = "\\p{InCombiningDiacriticalMarks}+".toRegex()

    private var voyageDao: VoyageDao? = null
    private var mBundleRecyclerViewState: Bundle? = null
    private var mListState: Parcelable? = null
    private var listVoyageTitle = arrayListOf<String>()

    private val filteredPosts: MutableList<Voyage> = mutableListOf()
    private val oldFilteredPosts: MutableList<Voyage> = mutableListOf()

    private var adapter: VoyageAdapter? = null

    private val disposable = CompositeDisposable()
    private var listVoyage: MutableList<Voyage>? = null


    private var destination = ""
    private var budget = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root: View = inflater.inflate(R.layout.fragment_trip, container, false)
        val voyageRecyclerview = root.findViewById<View>(R.id.voyage_recyclerview) as RecyclerView
        voyageRecyclerview.layoutManager = LinearLayoutManager(this.context)

        val searchText = root.findViewById<AutoCompleteTextView>(R.id.search_voyage)
        val fab: FloatingActionButton = root.findViewById(R.id.fab_add)


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

        runBlocking {
            listVoyage = voyageDao?.getVoyage()?.toMutableList()
            listVoyage?.map {
                listVoyageTitle.add(it.titre)
            }
            listVoyage?.let { oldFilteredPosts.addAll(it) }
            adapter = VoyageAdapter(oldFilteredPosts)
            voyageRecyclerview.adapter = adapter
        }

        searchText
            .textChanges()
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe {
                searchVoyages(it.toString())
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val diffResult = DiffUtil.calculateDiff(
                            VoyagesDiffUtilCallback(oldFilteredPosts, filteredPosts)
                        )
                        oldFilteredPosts.clear()
                        oldFilteredPosts.addAll(filteredPosts)
                        adapter?.let { it1 -> diffResult.dispatchUpdatesTo(it1) }
                        voyageRecyclerview.adapter = adapter
                    }.addTo(disposable)
            }.addTo(disposable)

        return root
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val voyages = voyageDao?.getVoyage()
            voyage_recyclerview.adapter = VoyageAdapter(voyages ?: emptyList())
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

    private fun searchVoyages(query: String): Completable = Completable.create {
        val wanted = listVoyage?.filter { itVoyage ->
            itVoyage.titre.unAccent().contains(query.unAccent(), true)
        }?.toList()

        filteredPosts.clear()
        if (!wanted.isNullOrEmpty()) {
            filteredPosts.addAll(wanted)
        }
        it.onComplete()
    }

    // Pour ignorer les accents dans l'input
    private fun CharSequence.unAccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return regexUnaccent.replace(temp, "")
    }


}

