package com.example.tripin

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.model.Activity
import kotlinx.android.synthetic.main.activity_detail_activites.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.act
import org.jetbrains.anko.imageResource

class DetailActivites : AppCompatActivity() {

    private var activite: Activity? = null
    private var id: Int = 0
    private var activityDao: ActivityDao? = null
    private var favoris: Boolean? = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_activites)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra("id", 0)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "allactivity")
                .build()

        activityDao = database.getActivityDao()
        runBlocking {
            activite = activityDao!!.getActivity(id)
            val url = activite?.cover_image_url
            Glide.with(this@DetailActivites)
                .load(url)
                .into(detail_activity_imageview)
            detail_activity_titre_textview.text = activite?.title
            detail_activity_dispo_textview.text = "Dispo : " + activite?.operational_days
            detail_activity_prix_textview.text = "Prix : " + activite?.formatted_iso_value
            detail_activity_about_textview.text = "Description : " + activite?.about

        }

        fab_fav.setOnClickListener {

            if (favoris == false) {
                fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)
                favoris = true
                activite?.favoris = true

            } else if (favoris == true) {
                fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                favoris = false
                activite?.favoris = false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

