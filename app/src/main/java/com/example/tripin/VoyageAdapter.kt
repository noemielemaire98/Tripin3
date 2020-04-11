package com.example.tripin

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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



    override fun onBindViewHolder(holder: VoyageViewHolder, position: Int) {
        val voyage = voyages[position]
        holder.voyageView.voyage_title_textview.text = "${voyage.titre}"
        holder.voyageView.voyage_date_textview.text = "Du ${voyage.date}"
        holder.voyageView.voyage_imageview.setImageResource(voyage.photo)

        holder.voyageView.setOnClickListener {
            Log.d("EPF", "$voyage")
            val intent= Intent(it.context,DetailVoyage::class.java)
            intent.putExtra("id",voyage.id)
            it.context.startActivity(intent)
        }
    }

}