package com.example.tripin.ui.find

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.tripin.*


/**
 * A simple [Fragment] subclass.
 */
class FindFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_find, container, false)


        val btFlight: Button = view.findViewById(R.id.bt_flight)

        btFlight.setOnClickListener {
            val intent = Intent(this.context, FindFlight::class.java)
            startActivity(intent)
        }

        val btHotel: Button = view.findViewById(R.id.bt_hotel)

        btHotel.setOnClickListener {
            val intent = Intent(this.context, FindHotel::class.java)
            startActivity(intent)
        }

        val btActivities: Button = view.findViewById(R.id.bt_activities)

        btActivities.setOnClickListener {
            val intent = Intent(this.context, FindActivites::class.java)
            startActivity(intent)
        }

        return view

    }
}

