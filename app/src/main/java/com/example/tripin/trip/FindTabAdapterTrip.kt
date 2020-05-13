package com.example.tripin.trip

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FindTabAdapterTrip (fm : FragmentManager): FragmentPagerAdapter(fm) {


    val fragment_list = arrayListOf<Fragment>()
    val fragment_title = arrayListOf<String>()


    override fun getItem(position: Int): Fragment {
        return fragment_list[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragment_title[position]
    }

    override fun getCount(): Int {
        return fragment_list.size
    }

    fun addFragment(frag : Fragment,title : String){
        fragment_list.add(frag)
        fragment_title.add(title)
    }
}