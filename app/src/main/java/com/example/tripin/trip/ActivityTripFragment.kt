package com.example.tripin.trip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.R
import com.example.tripin.find.activity.ActivityAdapter
import com.example.tripin.find.activity.FindActivitesActivity
import kotlinx.coroutines.runBlocking


class ActivityTripFragment : Fragment() {

    private var list_fav = arrayListOf<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val voyage = (activity as? DetailVoyage2)!!.voyage

        val view = inflater.inflate(R.layout.activity_saved_activites, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.activitiessaved_recyclerview)
        val iv = view.findViewById<ImageView>(R.id.noActivityImage)
        val rl = view.findViewById<RelativeLayout>(R.id.layout_nosavedActivities)

        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        iv.setOnClickListener {
            val intent = Intent(requireContext(), FindActivitesActivity::class.java)
            startActivity(intent)
        }

        runBlocking {
            val activities = voyage!!.list_activity
            activities?.map {
                list_fav.add(true)
            }
            if(activities!!.isEmpty()){
                rl.visibility = View.VISIBLE
            }else {
                rl.visibility = View.GONE
            }
           rv.adapter = ActivityAdapter(activities ?: emptyList(),list_fav)
        }




        return view
    }

    override fun onResume() {
        super.onResume()

/*        runBlocking {
            val activities = activityDaoSaved?.getActivity()
            activities?.map {
                list_fav.add(true)
            }

            layout_nosavedActivities.isVisible = activities!!.isEmpty()

            activitiessaved_recyclerview.adapter =
                ActivityAdapter(activities ?: emptyList(),list_fav)
        }*/
    }

}
