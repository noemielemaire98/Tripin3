package com.example.tripin.saved

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.find.activity.ActivityAdapter
import kotlinx.android.synthetic.main.activity_saved_activites.*
import kotlinx.coroutines.runBlocking

class SavedActivites : AppCompatActivity() {

    private var activityDao : ActivityDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_activites)

        activitiessaved_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "allactivity")
                .build()

        activityDao = database.getActivityDao()
        runBlocking {
            val activities = activityDao?.getActivity()
            activitiessaved_recyclerview.adapter =
                ActivityAdapter(activities!!)
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
            val activities = activityDao?.getActivity()
            activitiessaved_recyclerview.adapter =
                ActivityAdapter(activities!!)
        }
    }
}
