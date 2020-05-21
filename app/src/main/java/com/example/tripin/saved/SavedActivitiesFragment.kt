package com.example.tripin.saved

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.UserDao
import com.example.tripin.find.activity.ActivityAdapterGlobal
import com.example.tripin.model.User
import kotlinx.android.synthetic.main.activity_saved_activites.*
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass.
 */
class SavedActivitiesFragment : Fragment() {

    private var activityDaoSaved : ActivityDao? = null
    private var userDao: UserDao? = null
    private var list_fav = arrayListOf<Boolean>()
    private var username : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_saved_activites, container, false)
        val rv = root.findViewById<RecyclerView>(R.id.activitiessaved_recyclerview)
        val iv_noacivity = root.findViewById<ImageView>(R.id.noActivityImage)
        val layout_noactivities = root.findViewById<RelativeLayout>(R.id.layout_nosavedActivities)

        rv.layoutManager = LinearLayoutManager(requireActivity().baseContext, LinearLayoutManager.VERTICAL, false)

       val databasesaved =
            Room.databaseBuilder(requireActivity().baseContext, AppDatabase::class.java, "savedDatabase")
                .build()

        val databaseuser = Room.databaseBuilder(
            requireActivity(),
            AppDatabase::class.java,
            "allusers"
        ).build()

        userDao = databaseuser.getUserDao()
        iv_noacivity.setOnClickListener {
            val intent = Intent(requireActivity().baseContext, MainActivity::class.java)
            intent.putExtra("switchView", 3)
            startActivity(intent)
        }

        activityDaoSaved = databasesaved.getActivityDao()
        runBlocking {
            username = userDao?.getUser()?.uid ?: "Inconnu"
            val activities = activityDaoSaved?.getActivity()
            activities?.map {
                list_fav.add(true)
            }

            layout_noactivities.isVisible = activities!!.isEmpty()

            rv.adapter = ActivityAdapterGlobal(activities.toMutableList(),list_fav, username)
        }


        return root
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
                ActivityAdapterGlobal(activities.toMutableList(),list_fav, username)
        }
    }

}
