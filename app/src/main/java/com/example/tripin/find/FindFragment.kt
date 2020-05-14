package com.example.tripin.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.tripin.*
import com.example.tripin.find.activity.FindActivityFragment
import com.example.tripin.find.flight.FindFlightFragment
import com.example.tripin.find.hotel.FindHotelFragment
import com.example.tripin.find.voyage.FindVoyage
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import org.jetbrains.anko.toolbar


/**
 * A simple [Fragment] subclass.
 */
class FindFragment : Fragment() {

    private lateinit var viewpager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var myfragment: View


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myfragment = inflater.inflate(R.layout.fragment_find, container, false)
        viewpager = myfragment.findViewById(R.id.fragment_rechercheinterne)
        setupViewPager(viewpager)
        viewpager.offscreenPageLimit = 3
        tabLayout = myfragment.findViewById(R.id.tablayout_find)
        tabLayout.setupWithViewPager(viewpager)

        return myfragment

    }

    override fun onResume() {
        super.onResume()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        scope.launch {
            val adapter = FindTabAdapter(childFragmentManager)
            adapter.addFragment(FindVoyage(), "Voyage")
            adapter.addFragment(FindFlightFragment(), "Vol")
            adapter.addFragment(FindHotelFragment(), "Hotel")
            adapter.addFragment(FindActivityFragment(), "Activites")
            withContext(Dispatchers.Main) {
                viewPager.adapter = adapter
            }
        }

    }
}

