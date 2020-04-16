package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.Activity

@Dao
interface ActivityDao {

    @Query("select * from allactivity")
    suspend fun getActivity() : List<Activity>

    @Insert
    suspend fun addActivity(activity: Activity)

    @Query("delete from allactivity")
    suspend fun deleteActivity()
}