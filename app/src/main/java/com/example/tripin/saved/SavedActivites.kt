package com.example.tripin.saved

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.find.activity.ActivityAdapterGlobal
import com.example.tripin.find.activity.FindActivitesActivity
import kotlinx.android.synthetic.main.activity_saved_activites.*
import kotlinx.coroutines.runBlocking

class SavedActivites : AppCompatActivity() {

    private var activityDaoSaved : ActivityDao? = null
    private var list_fav = arrayListOf<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_activites)

        activitiessaved_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()

        noActivityImage.setOnClickListener {
            val intent = Intent(this, FindActivitesActivity::class.java)
            finish()
            startActivity(intent)
        }

        activityDaoSaved = databasesaved.getActivityDao()
        runBlocking {
            val activities = activityDaoSaved?.getActivity()
            activities?.map {
                list_fav.add(true)
            }

            layout_nosavedActivities.isVisible = activities!!.isEmpty()

            activitiessaved_recyclerview.adapter = ActivityAdapterGlobal(activities ?: emptyList(),list_fav)
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

    override fun onResume() {
        super.onResume()

        runBlocking {
            val activities = activityDaoSaved?.getActivity()
            activities?.map {
                list_fav.add(true)
            }

            layout_nosavedActivities.isVisible = activities!!.isEmpty()

            activitiessaved_recyclerview.adapter =
                ActivityAdapterGlobal(activities ?: emptyList(),list_fav)
        }
    }
}
