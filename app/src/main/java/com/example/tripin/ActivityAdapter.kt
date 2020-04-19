package com.example.tripin

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tripin.DetailVoyage
import com.example.tripin.R
import com.example.tripin.model.Activity
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.activities_view.view.*
import kotlinx.android.synthetic.main.voyage_view.view.*
import java.io.InputStream
import java.net.URL

class ActivityAdapter(val activities:List<Activity>) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(val activtyView : View) : RecyclerView.ViewHolder(activtyView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.activities_view, parent, false)
        return ActivityViewHolder(view)
    }

    override fun getItemCount(): Int = activities.size



    override fun onBindViewHolder(holder:ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.activtyView.activity_title_textview.text = "${activity.title}"
        val url = activity.cover_image_url
        Glide.with(holder.activtyView)
            .load(url)
            .centerCrop()
            .into(holder.activtyView.activity_imageview)

        holder.activtyView.activity_price_textview.text = "Prix : ${activity.formatted_iso_value}"
        holder.activtyView.activity_days_textview.text = "Dispo : ${activity.operational_days}"





    }

}