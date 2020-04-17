package com.example.tripin


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.model.Hotel
import kotlinx.android.synthetic.main.hotel_view.view.*

class HotelsAdapter (val hotels : List<Hotel>): RecyclerView.Adapter<HotelsAdapter.HotelViewHolder>(){

    class HotelViewHolder(val hotelView : View):RecyclerView.ViewHolder(hotelView)

    override fun getItemCount(): Int = hotels.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.hotel_view, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = hotels[position]
            holder.hotelView.hotels_name_textview.text= "${hotel.hotelName}"
      //  holder.hotelView.hotels_description_textview.text="${hotel.hotelDescription}"
            holder.hotelView.hotels_rate_textview.text="${hotel.rate}"
            holder.hotelView.hotels_imageview.setImageResource(R.drawable.ic_flight_black_24dp)
        Log.d("hotels", hotels.toString())


    }
    }
