package com.example.tripin.find.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.model.Activity
import com.example.tripin.saved.SavedActivites
import kotlinx.android.synthetic.main.activities_view.view.*
import kotlinx.android.synthetic.main.activity_detail_activites.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.matchParent

class ActivityAdapter(val list_activity: List<Activity>, val attribut_favoris : ArrayList<Boolean>) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(val activtyView : View) : RecyclerView.ViewHolder(activtyView)

    private var activityDaoSearch : ActivityDao? = null
    private var activityDaoSaved : ActivityDao? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.activities_view, parent, false)
        val databasesearch =
            Room.databaseBuilder(parent.context, AppDatabase::class.java, "searchDatabase")
                .build()
        val databasesaved =
            Room.databaseBuilder(parent.context, AppDatabase::class.java, "savedDatabase")
                .build()

        activityDaoSearch = databasesearch.getActivityDao()
        activityDaoSaved = databasesaved.getActivityDao()

        context = parent.context

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
        //holder.activtyView.activity_days_textview.text = "Dispo : ${activity.operational_days}"

        if(attribut_favoris[position] == true){
            holder.activtyView.fab_favActivity.setImageResource(R.drawable.ic_favorite_black_24dp)

        }


        holder.activtyView.fab_favActivity.setOnClickListener {
            if(attribut_favoris[position] == true){
                holder.activtyView.fab_favActivity.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                runBlocking {
                    activityDaoSaved?.deleteActivity(activity.uuid)
                }
                attribut_favoris[position] = false
                Toast.makeText(context, "L'activité a bien été supprimé des favoris", Toast.LENGTH_SHORT).show()

            }else {
                holder.activtyView.fab_favActivity.setImageResource(R.drawable.ic_favorite_black_24dp)
                runBlocking {
                    activityDaoSaved?.addActivity(activity)
                }

                attribut_favoris[position] = false
                Toast.makeText(context, "L'activité a bien été ajoutée aux favoris", Toast.LENGTH_SHORT).show()
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