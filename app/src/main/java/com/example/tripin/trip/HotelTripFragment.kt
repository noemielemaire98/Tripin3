package com.example.tripin.trip

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.find.hotel.HotelAdapterTrip
import com.example.tripin.saved.DetailVoyageSave
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class HotelTripFragment : Fragment() {


    private var list_fav = arrayListOf<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val voyage = if (activity is DetailVoyage2) {
            (activity as? DetailVoyage2)!!.voyage
        } else {
            (activity as? DetailVoyageSave)!!.voyage
        }

        val view = inflater.inflate(R.layout.fragment_hotel_trip, container, false)
        val recyclerviewHotel = view.findViewById<RecyclerView>(R.id.hotelsaved_recyclerview)
        val imageNoHotel = view.findViewById<ImageView>(R.id.noHotelImage)
        val layoutNoHotel = view.findViewById<RelativeLayout>(R.id.layout_nosavedHotel)


        recyclerviewHotel.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        imageNoHotel.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("switchView", 2)
            startActivity(intent)
            activity?.finish()
        }

        runBlocking {
            val hotels = voyage!!.list_hotels

            hotels?.map {
                list_fav.add(true)
            }
            if (hotels!!.isEmpty()) {
                layoutNoHotel.visibility = View.VISIBLE
            } else {
                layoutNoHotel.visibility = View.GONE
                val hotels2 = hotels.toMutableList()
                recyclerviewHotel.adapter =
                    HotelAdapterTrip(hotels2, voyage, list_fav, layoutNoHotel)
            }
        }
        return view
    }

}
