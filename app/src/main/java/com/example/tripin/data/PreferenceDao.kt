package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tripin.model.Preference

@Dao
interface PreferenceDao {
    @Query("select * from allpreferences")
    suspend fun getPreference() : Preference

    @Query("delete from allpreferences")
    suspend fun deletePreferences()

    @Insert
    suspend fun addPreference(preference: Preference)

    @Query("select * from allpreferences where destination = :destination")
    suspend fun getPreference(destination: String) : Preference

    @Update
    suspend fun updatePreference(preference: Preference)
}