package com.example.tripin.find.hotel

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tripin.R
import com.example.tripin.model.Offer
import kotlinx.android.synthetic.main.offer_view.view.*

class OffersAdapter (val offers : List<Offer>): RecyclerView.Adapter<OffersAdapter.OfferViewHolder>() {

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
        val offer = offers[position]
        Log.d("offerAdapter", offer.toString())
        holder.offerView.price_offer_recyclerview.text = "${offer.price}"
    }

}