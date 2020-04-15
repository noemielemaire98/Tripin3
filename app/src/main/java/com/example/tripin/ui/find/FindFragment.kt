package com.example.tripin.ui.find

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.*
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.model.Flight
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_find.*
import kotlinx.android.synthetic.main.fragment_find.view.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class FindFragment : Fragment() {


<<<<<<< HEAD
    private lateinit var dashboardViewModel: FindViewModel


    @SuppressLint("SimpleDateFormat")
=======
>>>>>>> d641792498f0cd9edb0aa2a75b8cc70ddfaf8e9b
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_find, container, false)


<<<<<<< HEAD
        val bt_flight: Button = view.findViewById(R.id.bt_find_fight)
=======
        val bt_flight: Button = view.findViewById(R.id.bt_flight)
>>>>>>> d641792498f0cd9edb0aa2a75b8cc70ddfaf8e9b

        bt_flight.setOnClickListener { view ->
            val intent = Intent(this.context, FindFlight::class.java)
            startActivity(intent)
            true
        }
        val bt_hotel: Button = view.findViewById(R.id.bt_find_hotel)

<<<<<<< HEAD
=======
        val bt_hotel: Button = view.findViewById(R.id.bt_hotel)

>>>>>>> d641792498f0cd9edb0aa2a75b8cc70ddfaf8e9b
        bt_hotel.setOnClickListener { view ->
            val intent = Intent(this.context, FindHotel::class.java)
            startActivity(intent)
            true
        }
        val bt_activities: Button = view.findViewById(R.id.bt_find_activity)

<<<<<<< HEAD
        bt_activities.setOnClickListener { view ->
            val intent = Intent(this.context, FindActivities::class.java)
            startActivity(intent)
            true
        }

        return view


    }
}




=======
        val bt_activites: Button = view.findViewById(R.id.bt_activities)

        bt_activites.setOnClickListener { view ->
            val intent = Intent(this.context, FindActivites::class.java)
            startActivity(intent)
            true
        }

        return view

    }
}

>>>>>>> d641792498f0cd9edb0aa2a75b8cc70ddfaf8e9b
