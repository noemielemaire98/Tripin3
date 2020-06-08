package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tripin.model.Activity

@Dao
interface ActivityDao {

    @Query("select * from allActivities")
    suspend fun getActivity() : List<Activity>

    @Insert
    suspend fun addActivity(activity: Activity)

    @Query("delete from allActivities")
    suspend fun deleteActivity()

    @Query("select * from allActivities where uuid = :id")
    suspend fun getActivity(id: String) : Activity

    //@Query("update allActivities set favoris= :fav where uuid = :id")
    //suspend fun updateActivity(fav: Boolean,id: String)

    @Query("delete from allActivities where uuid = :id")
    suspend fun deleteActivity(id : String)


}