package com.example.tripin.find.activity

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.model.Activity
import kotlinx.android.synthetic.main.activities_view.view.*

class ActivityAdapter(val list_activity: List<Activity>) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(val activtyView : View) : RecyclerView.ViewHolder(activtyView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.activities_view, parent, false)
        return ActivityViewHolder(
            view
        )
    }

    override fun getItemCount(): Int = list_activity.size



    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity =list_activity[position]
        holder.activtyView.activity_title_textview.text = "${activity.title}"
        val url = activity.cover_image_url
        Glide.with(holder.activtyView)
            .load(url)
            .centerCrop()
            .into(holder.activtyView.activity_imageview)

        holder.activtyView.activity_price_textview.text = "Prix : ${activity.formatted_iso_value}"
        holder.activtyView.activity_days_textview.text = "Dispo : ${activity.operational_days}"

        holder.activtyView.setOnClickListener {
            //Log.d("CCC", "$activity")
            val intent= Intent(it.context, DetailActivites::class.java)
            intent.putExtra("a",activity)
            it.context.startActivity(intent)
        }



    }

}