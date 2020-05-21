package com.example.tripin.find.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.model.Activity
import kotlinx.android.synthetic.main.activities_view.view.*
import kotlinx.coroutines.runBlocking

class ActivityAdapterGlobal(val list_activity: MutableList<Activity>, val attribut_favoris : ArrayList<Boolean>, val username : String) : RecyclerView.Adapter<ActivityAdapterGlobal.ActivityViewHolder>() {

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
        /*if (activity.operational_days != null){
            holder.activtyView.activity_days_textview.text = "Dispo : ${activity.operational_days}"
        }else {
            holder.activtyView.activity_days_textview.text = "Dispo : non communiquées"
        }*/

        if(activity.reviews_avg != 0.0){
            holder.activtyView.layout_activity_rate.visibility = View.VISIBLE
            holder.activtyView.activity_rate_textview.text = "${activity.reviews_avg}"
        }

        //holder.activtyView.activity_category_textview.text = "${activity.category}"


        if(attribut_favoris[position] == true){
            holder.activtyView.fab_favActivity.setImageResource(R.drawable.ic_favorite_black_24dp)

        }


        holder.activtyView.rv_categorie.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.activtyView.rv_categorie.adapter =
            CategoryAdapter(activity.category  ?: emptyList())



        // Listener sur les favoris

        holder.activtyView.fab_favActivity.setOnClickListener {
            if(attribut_favoris[position] == true){
                holder.activtyView.fab_favActivity.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                runBlocking {
                    val activitybis = activityDaoSaved?.getActivity(activity.uuid)
                    val list_users = activitybis?.users
                    val newList = list_users?.minus(username) ?: emptyList()

                    val id = activitybis?.uuid ?: ""
                    val title = activitybis?.title ?: ""
                    val cover_image_url = activitybis?.cover_image_url
                    val formatted_iso_value = activitybis?.formatted_iso_value
                    val operational_days = activitybis?.operational_days
                    val reviews_avg = activitybis?.reviews_avg
                    val category = activitybis?.category ?: emptyList()
                    val url = activitybis?.url
                    val top_seller = activitybis?.top_seller ?: false
                    val must_see = activitybis?.must_see ?: false
                    val description = activitybis?.description
                    val about = activitybis?.about
                    val latitude= activitybis?.latitude ?: 0.0
                    val longitude = activitybis?.longitude ?: 0.0

                    val newActivity = Activity(id, title, cover_image_url, formatted_iso_value, operational_days, reviews_avg, category, url, top_seller, must_see, description, about, latitude, longitude, newList)
                    activityDaoSaved?.updateActivity(newActivity)
                }
                attribut_favoris[position] = false
                list_activity.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,list_activity.size)

                Toast.makeText(context, "L'activité a bien été supprimé des favoris", Toast.LENGTH_SHORT).show()

            }else {
                holder.activtyView.fab_favActivity.setImageResource(R.drawable.ic_favorite_black_24dp)
                runBlocking {
                    val activitybis = activityDaoSaved?.getActivity(activity.uuid)
                    val list_users = activitybis?.users
                    val newList = list_users?.plus(username) ?: emptyList()

                    val id = activitybis?.uuid ?: ""
                    val title = activitybis?.title ?: ""
                    val cover_image_url = activitybis?.cover_image_url
                    val formatted_iso_value = activitybis?.formatted_iso_value
                    val operational_days = activitybis?.operational_days
                    val reviews_avg = activitybis?.reviews_avg
                    val category = activitybis?.category ?: emptyList()
                    val url = activitybis?.url
                    val top_seller = activitybis?.top_seller ?: false
                    val must_see = activitybis?.must_see ?: false
                    val description = activitybis?.description
                    val about = activitybis?.about
                    val latitude= activitybis?.latitude ?: 0.0
                    val longitude = activitybis?.longitude ?: 0.0

                    val newActivity = Activity(id, title, cover_image_url, formatted_iso_value, operational_days, reviews_avg, category, url, top_seller, must_see, description, about, latitude, longitude, newList)
                    activityDaoSaved?.updateActivity(newActivity)
                }
                attribut_favoris[position] = true
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