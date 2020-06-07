package com.example.tripin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity (tableName = "allhotels")
data class Hotel(
    @PrimaryKey val hotelId: Int,
    val hotelName: String,
    val hotelDescription: String?,
    val rate: Int?,
    val image_url: String?,
    @TypeConverters(Converters:: class)
    val adresse: MutableList<String>,
    val latitude: Double,
    val longitude: Double,
    val prix: String,
    @TypeConverters(Converters:: class)
    val equipements: MutableList<String>?

) : Parcelable {
    companion object {
        val all = (1..20).map {
            Hotel(
                it,
                "Name$it",
                "Description$it",
                it,
                "R.drawable.hotel",
                mutableListOf("A", "B", "C"),
                0.1,
                0.1,
                "0.1",
                mutableListOf("A", "B", "C")
            )
        }.toMutableList()
    }





}
    public class Converters {

        @TypeConverter
        fun fromString(value: String?): MutableList<String>? {
            if (value == null) return null
            else {
                val listType = object : TypeToken<MutableList<String?>?>() {}.type
                return Gson().fromJson(value, listType)
            }

        }

        @TypeConverter
        fun listToString(list: MutableList<String>?): String? {
            val gson = Gson()
            return gson.toJson(list)
        }

    }





