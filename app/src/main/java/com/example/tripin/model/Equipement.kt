package com.example.tripin.model

import androidx.room.Entity

@Entity(tableName = "equipements")

data class Equipement(
    var heading  : String,
    var listItem : List<String>
)