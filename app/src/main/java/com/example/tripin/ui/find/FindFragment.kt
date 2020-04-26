package com.example.tripin.ui.find

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.tripin.*
import com.google.android.material.tabs.TabLayout


/**
 * A simple [Fragment] subclass.
 */
class FindFragment : Fragment() {

    private lateinit var viewpager : ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var myfragment : View


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myfragment = inflater.inflate(R.layout.fragment_find, container, false)
        viewpager = myfragment.findViewById(R.id.fragment_rechercheinterne)
        setupViewPager(viewpager);
        tabLayout = myfragment.findViewById(R.id.tablayout_find)
        tabLayout.setupWithViewPager(viewpager)

        return myfragment

    }

    fun setupViewPager(viewPager : ViewPager){
        var adapter : FindTabAdapter = FindTabAdapter(childFragmentManager)
        adapter.addFragment(FindVoyage(),"Voyage")
        adapter.addFragment(FindFlight2(),"Vol")
        adapter.addFragment(FindHotel2(),"Hotel")
        adapter.addFragment(FindActivity2(),"Activites")
        viewPager.adapter = adapter

    }
}

