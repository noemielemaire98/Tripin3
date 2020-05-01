package com.example.tripin.saved

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.tripin.*

class SavedFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_saved, container, false)

        val btFlight: Button = root.findViewById(R.id.bt_flight_saved)

        btFlight.setOnClickListener {
            val intent = Intent(this.context, SavedFlight::class.java)
            startActivity(intent)
        }

        val btHotel: Button = root.findViewById(R.id.bt_hotel_saved)

        btHotel.setOnClickListener {
            val intent = Intent(this.context, SavedHotel::class.java)
            startActivity(intent)
        }

        val btActivities: Button = root.findViewById(R.id.bt_activities_saved)

        btActivities.setOnClickListener {
            val intent = Intent(this.context, SavedActivites::class.java)
            startActivity(intent)
        }


        return root
    }
}
