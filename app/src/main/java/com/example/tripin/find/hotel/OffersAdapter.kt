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

class OffersAdapter (val offers : List<Rooms>): RecyclerView.Adapter<OffersAdapter.OfferViewHolder>() {

    private lateinit var context: Context

    class OfferViewHolder(val offerView: View) : RecyclerView.ViewHolder(offerView)

    override fun getItemCount(): Int = offers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.offer_view, parent, false)

        context = parent.context

        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val offer = offers?.get(position)
        holder.offerView.price_room_recyclerview.text = "${offer?.price}"
        holder.offerView.name_room_recyclerview.text = offer.nameRoom


        holder.offerView.setOnClickListener {
            val intent= Intent(it.context, DetailsRoom::class.java)
            intent.putExtra("room", offer)
            it.context.startActivity(intent)
        }

    }

}