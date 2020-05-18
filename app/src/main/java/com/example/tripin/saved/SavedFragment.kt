package com.example.tripin.saved

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.tripin.*
import com.example.tripin.find.FindTabAdapter
import com.example.tripin.find.activity.FindActivityFragment
import com.example.tripin.find.flight.FindFlightFragment
import com.example.tripin.find.hotel.FindHotelFragment
import com.example.tripin.find.voyage.FindVoyage
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*

class SavedFragment : Fragment() {

    private lateinit var viewpager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_saved, container, false)

        viewpager = root.findViewById<ViewPager>(R.id.fragment_rechercheinterne_saved)
        setupViewPager(viewpager)
        viewpager.offscreenPageLimit = 3
        val switchView = arguments?.getInt("switchView")
        if (switchView == 1 || switchView == 2 || switchView == 3) {
            Handler().postDelayed({
                viewpager.setCurrentItem(switchView, false)
            }, 1)
        }
        tabLayout = root.findViewById<TabLayout>(R.id.tablayout_saved)
        tabLayout.setupWithViewPager(viewpager)


        return root
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        scope.launch {
            val adapter = FindTabAdapter(childFragmentManager)
            adapter.addFragment(SavedVoyage(), "Voyage")
            adapter.addFragment(SavedFlightFragment(), "Vol")
            adapter.addFragment(SavedHotelFragment(), "Hotel")
            adapter.addFragment(SavedActivitiesFragment(), "Activité")

            withContext(Dispatchers.Main) {
                viewPager.adapter = adapter
            }
        }

    }
}
