package com.example.tripin.find.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.model.Activity
import kotlinx.android.synthetic.main.activity_detail_activites.*
import kotlinx.coroutines.runBlocking

class DetailActivites : AppCompatActivity() {

    private var activite: Activity? = null
    private var id: Int = 0
    private var activityDaoSaved: ActivityDao? = null
    private var favoris : Boolean = false
    private var list_activities_bdd = emptyList<Activity>()



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_activites)

        // BOUTON RETOUR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ON RECUPERE L'ACTIVITE CHOISIT DANS LA RECYCLERVIEW
       activite = intent.getParcelableExtra("activity")
       favoris = intent.getBooleanExtra("attribut_fav",false)


        // ON APPELLE LA BDD
        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()
        activityDaoSaved = databasesaved.getActivityDao()


        // ON SET LES ATTRIBUTS
            val url = activite?.cover_image_url
            Glide.with(this@DetailActivites)
                .load(url)
                .into(detail_activity_imageview)
        runBlocking {
            list_activities_bdd = activityDaoSaved!!.getActivity()
        }
        list_activities_bdd?.forEach {
            if (it.title == activite!!.title) {
                favoris = true
            }
        }

        if(favoris == true){
            fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)

        }else if (favoris == false){
            fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)

        }

            detail_activity_titre_textview.text = activite?.title
            detail_activity_dispo_textview.text = "Dispo : " + activite?.operational_days
            detail_activity_prix_textview.text = "Prix : " + activite?.formatted_iso_value
            detail_activity_about_textview.text = activite?.about


        // 3 - AU CLIC SUR LE BOUTON
        fab_fav.setOnClickListener {
            if (favoris == false) {
                runBlocking {
                        activityDaoSaved?.addActivity(activite!!)
                }
                favoris = true
                fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)
                Toast.makeText(this, "L'activité a bien été ajoutée aux favoris", Toast.LENGTH_SHORT).show()

            } else if (favoris == true) {

                runBlocking {
                activityDaoSaved?.deleteActivity(activite!!.uuid)
                }
                favoris = false
                fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                Toast.makeText(this, "L'activité a bien été supprimé des favoris", Toast.LENGTH_SHORT).show()
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

