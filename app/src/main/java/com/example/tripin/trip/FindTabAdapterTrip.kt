package com.example.tripin.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class FindTabAdapterTrip (fm : FragmentManager, data: Bundle): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    val fragment_list = arrayListOf<Fragment>()
    val fragment_title = arrayListOf<String>()
    private val fragmentBundle: Bundle = data

    override fun getItem(position: Int): Fragment {
        fragment_list[position].arguments = fragmentBundle
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