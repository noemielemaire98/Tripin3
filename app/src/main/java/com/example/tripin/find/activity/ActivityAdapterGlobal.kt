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
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.model.Activity
import com.example.tripin.saved.SavedActivitiesFragment
import kotlinx.android.synthetic.main.activities_view.view.*
import kotlinx.coroutines.runBlocking

class ActivityAdapterGlobal(val list_activity: MutableList<Activity>, val attribut_favoris : ArrayList<Boolean>) : RecyclerView.Adapter<ActivityAdapterGlobal.ActivityViewHolder>() {

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

        holder.activtyView.activity_price_textview.text = "A partir de ${activity.formatted_iso_value}"
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
            CategoryAdapter(activity.category)



        // Listener sur les favoris

        holder.activtyView.fab_favActivity.setOnClickListener {
            if(attribut_favoris[position]){
                holder.activtyView.fab_favActivity.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                runBlocking {
                    activityDaoSaved?.deleteActivity(activity.uuid)
                }
                attribut_favoris[position] = false

                val fragmentFavorisViewPager = try {
                    // https://stackoverflow.com/a/54829516/13289762
                    (context as MainActivity).supportFragmentManager.fragments[0].childFragmentManager.fragments[0]?.childFragmentManager?.fragments
                        ?.get(3)?.javaClass?.simpleName
                } catch (ex: Exception) {
                    ""
                }

                if (fragmentFavorisViewPager == SavedActivitiesFragment().javaClass.simpleName) {
                    list_activity.removeAt(position)
                    attribut_favoris.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, list_activity.size)

                    Toast.makeText(
                        context,
                        "L'activité a bien été supprimé des favoris",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }else {
                holder.activtyView.fab_favActivity.setImageResource(R.drawable.ic_favorite_black_24dp)
                runBlocking {
                    activityDaoSaved?.addActivity(activity)
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