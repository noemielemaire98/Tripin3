package com.example.tripin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tripin.model.User

@Dao
interface UserDao {
    @Query("select * from allusers")
    suspend fun getUser() : User

    @Query("delete from allusers")
    suspend fun deleteUser()

    @Insert
    suspend fun addUser(preference: User)

    @Query("select * from allusers where username = :uid")
    suspend fun getUser(uid: String) : User

}