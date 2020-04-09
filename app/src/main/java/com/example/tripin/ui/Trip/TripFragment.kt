package com.example.tripin.ui.Trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.R
import com.example.tripin.VoyageAdapter
import com.example.tripin.model.Voyage

class TripFragment : Fragment() {

    private lateinit var dashboardViewModel: TripViewModel

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
        voyage_recyclerview.adapter = VoyageAdapter(Voyage.all)
        return root
    }
}

