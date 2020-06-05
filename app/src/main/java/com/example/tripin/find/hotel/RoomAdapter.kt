package com.example.tripin.find.hotel

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.R
import com.example.tripin.model.Offer
import com.example.tripin.model.Rooms
import kotlinx.android.synthetic.main.offer_view.view.*

class RoomAdapter (val rooms : List<Rooms>): RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private lateinit var context: Context

    class RoomViewHolder(val roomView: View) : RecyclerView.ViewHolder(roomView)

    override fun getItemCount(): Int = rooms.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.offer_view, parent, false)

        context = parent.context

        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms?.get(position)
        holder.roomView.price_room_recyclerview.text = "${room?.priceNight}/nuit"
        holder.roomView.name_room_recyclerview.text = room.nameRoom


        holder.roomView.setOnClickListener {
            val intent= Intent(it.context, DetailsRoom::class.java)
            intent.putExtra("room", room)
            it.context.startActivity(intent)
        }

    }

}