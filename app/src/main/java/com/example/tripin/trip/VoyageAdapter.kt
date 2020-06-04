package com.example.tripin.trip

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.voyage_view.view.*

class VoyageAdapter(val voyages:List<Voyage>) : RecyclerView.Adapter<VoyageAdapter.VoyageViewHolder>() {
    class VoyageViewHolder(val voyageView : View) : RecyclerView.ViewHolder(voyageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoyageViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.voyage_view, parent, false)
        return VoyageViewHolder(view)
    }

    override fun getItemCount(): Int = voyages.size



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VoyageViewHolder, position: Int) {
        val voyage = voyages[position]


        holder.voyageView.voyage_title_textview.text = voyage.titre
        holder.voyageView.voyage_date_textview.text = "Du ${voyage.date}"
        holder.voyageView.voyage_dateRetour_textview.text = "Au ${voyage.dateRetour}"
        holder.voyageView.voyage_nb_voyageur_textview.text = "Nombre de voyageurs : ${voyage.nb_voyageur}"

        if (voyage.photo != "") {
            val url = voyage.photo
            Glide.with(holder.voyageView)
                .load(url)
                .centerCrop()
                .into(holder.voyageView.voyage_imageview)
        }else {
            holder.voyageView.voyage_imageview.setImageResource(R.drawable.destination1)
        }


        holder.voyageView.setOnClickListener {
            val intent= Intent(it.context, DetailVoyage2::class.java)
            intent.putExtra("id",voyage.id)
            it.context.startActivity(intent)

        }
    }

}