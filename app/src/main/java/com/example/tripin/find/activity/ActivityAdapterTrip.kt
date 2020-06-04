package com.example.tripin.find.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Activity
import com.example.tripin.model.Voyage
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activities_view.view.*
import kotlinx.android.synthetic.main.activities_view.view.activity_imageview
import kotlinx.android.synthetic.main.activities_view.view.activity_price_textview
import kotlinx.android.synthetic.main.activities_view.view.activity_rate_textview
import kotlinx.android.synthetic.main.activities_view.view.activity_title_textview
import kotlinx.android.synthetic.main.activities_view.view.layout_activity_rate
import kotlinx.android.synthetic.main.activitiestrip_view.view.*
import kotlinx.coroutines.runBlocking

class ActivityAdapterTrip(val list_activity: MutableList<Activity>, val attribut_favoris : ArrayList<Boolean>,var voyage: Voyage,val listMarker : ArrayList<Marker>, val layout_noactivities : RelativeLayout) : RecyclerView.Adapter<ActivityAdapterTrip.ActivityViewHolder>() {

    class ActivityViewHolder(val activtyView : View) : RecyclerView.ViewHolder(activtyView)

    private var voyageDao : VoyageDao? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.activitiestrip_view, parent, false)
        val databasesaved =
            Room.databaseBuilder(parent.context, AppDatabase::class.java, "savedDatabase")
                .build()

        voyageDao = databasesaved.getVoyageDao()

        context = parent.context

        return ActivityViewHolder(
            view
        )
    }

    override fun getItemCount(): Int = list_activity.size



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {

        val activity =list_activity[position]
        holder.activtyView.activity_title_textview.text = activity.title
        val url = activity.cover_image_url
        Glide.with(holder.activtyView)
            .load(url)
            .centerCrop()
            .into(holder.activtyView.activity_imageview)

        holder.activtyView.activity_price_textview.text = "Prix : ${activity.formatted_iso_value}"

        if(activity.reviews_avg != 0.0){
            holder.activtyView.layout_activity_rate.visibility = View.VISIBLE
            holder.activtyView.activity_rate_textview.text = "${activity.reviews_avg}"
        }

        holder.activtyView.bt_delete.setOnClickListener {
            list_activity.removeAt(position)
            voyage.list_activity = list_activity.toList()
            runBlocking {
                voyageDao?.updateVoyage(voyage)
            }
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,list_activity.size)
            val marker = listMarker[position]
            marker.remove()

            if(list_activity.isEmpty()){
               layout_noactivities.visibility = View.VISIBLE
            }
        }

        holder.activtyView.setOnClickListener {
            val intent= Intent(it.context, DetailActivites::class.java)
            intent.putExtra("activity",activity)
            intent.putExtra("attribut_fav",attribut_favoris[position])
            it.context.startActivity(intent)
        }



    }

}