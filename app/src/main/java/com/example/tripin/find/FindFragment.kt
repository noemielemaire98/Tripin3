package com.example.tripin.find

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.tripin.R
import com.example.tripin.data.VoyageDao
import com.example.tripin.find.activity.FindActivityFragment
import com.example.tripin.find.flight.FindFlightFragment
import com.example.tripin.find.hotel.FindHotelFragment
import com.example.tripin.find.voyage.FindVoyage
import com.example.tripin.model.Voyage
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 */
class FindFragment : Fragment() {

    private lateinit var viewpager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var myfragment: View
    var voyage: Voyage?=null
    private var voyageDao : VoyageDao? = null
    private var bundle = Bundle()


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

//         id =  .getIntExtra("id",0)

        val id =arguments?.getInt("id")

        if (id != null)
        bundle.putInt("id", id)


        val switchView = arguments?.getInt("switchView")
        if ( switchView == 1 || switchView == 2 || switchView == 3) {
            Handler().postDelayed({
                viewpager.setCurrentItem(switchView, false)
            }, 1)
        }else if (switchView == 4 ){
            Handler().postDelayed({
                viewpager.setCurrentItem(0, false)
            }, 1)
        }
        tabLayout = myfragment.findViewById(R.id.tablayout_find)
        tabLayout.setupWithViewPager(viewpager)

        return myfragment

    }

    private fun setupViewPager(viewPager: ViewPager) {
        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        scope.launch {
            val adapter = FindTabAdapter(childFragmentManager, bundle)
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