package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.Activity
import com.example.tripin.model.Voyage

@Dao
interface ActivityDao {

    @Query("select * from allactivity")
    suspend fun getActivity() : List<Activity>

    @Insert
    suspend fun addActivity(activity: Activity)

    @Query("delete from allactivity")
    suspend fun deleteActivity()

    @Query("select * from allactivity where uuid = :id")
    suspend fun getActivity(id: String) : Activity

    @Query("update allactivity set favoris= :fav where uuid = :id")
    suspend fun updateActivity(fav: Boolean,id: String)

    @Query("delete from allactivity where uuid = :id")
    suspend fun deleteActivity(id : String)


}