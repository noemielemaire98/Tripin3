package com.example.tripin.saved

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.UserDao
import com.example.tripin.find.activity.ActivityAdapterGlobal
import com.example.tripin.find.activity.FindActivitesActivity
import kotlinx.android.synthetic.main.activity_saved_activites.*
import kotlinx.coroutines.runBlocking

class SavedActivites : AppCompatActivity() {

    private var activityDaoSaved : ActivityDao? = null
    private var userDao: UserDao? = null
    private var list_fav = arrayListOf<Boolean>()
    var username: String = "Inconnu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_activites)

        activitiessaved_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()

        val databaseuser = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "allusers"
        ).build()

        userDao = databaseuser.getUserDao()

        noActivityImage.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("switchView", 3)
            startActivity(intent)
            finish()
        }

        activityDaoSaved = databasesaved.getActivityDao()
        runBlocking {
            username = userDao?.getUser()?.uid ?: "Inconnu"
            val list_users = emptyList<String>().plus(username)
            val activities = activityDaoSaved?.getActivityByUser(list_users)
            activities?.map {
                list_fav.add(true)
            }

            layout_nosavedActivities.isVisible = activities!!.isEmpty()

            activitiessaved_recyclerview.adapter = ActivityAdapterGlobal(activities.toMutableList(),list_fav, username)
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
            username = userDao?.getUser()?.uid ?: "Inconnu"
            val list_users = emptyList<String>().plus(username)
            val activities = activityDaoSaved?.getActivityByUser(list_users)
            activities?.map {
                list_fav.add(true)
            }

            layout_nosavedActivities.isVisible = activities!!.isEmpty()

            activitiessaved_recyclerview.adapter =
                ActivityAdapterGlobal(activities.toMutableList(),list_fav, username)
        }
    }
}
