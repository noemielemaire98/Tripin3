package com.example.tripin.data

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.tripin.model.*


@Database(
    entities = [Voyage::class, Flight::class, Activity::class, Hotel::class, City::class, Offer::class],
    version = 1
)

@TypeConverters(
    Converters::class,
    ListActivityConverter::class,
    ListFlightsConverter::class,
    ListHotelsConverter::class
)


abstract class AppDatabase : RoomDatabase() {

    abstract fun getVoyageDao(): VoyageDao

    abstract fun getFlightDao(): FlightDao

    abstract fun getActivityDao(): ActivityDao

    abstract fun getHotelDao(): HotelDao

    abstract fun getCityDao(): CityDao

    abstract fun getOfferDao(): OfferDao


}


class ListActivityConverter {
    @TypeConverter
    fun fromListActivity(listActivity: List<Activity>): List<String> {
        val arrayList = arrayListOf<String>()
        listActivity.map {
            val string =
                "${it.uuid};${it.title};${it.cover_image_url};${it.formatted_iso_value};${it.operational_days};${it.reviews_avg};${it.category};${it.url};${it.top_seller};${it.must_see};${it.description};${it.about};${it.latitude};${it.longitude}"
            arrayList.add(string)
        }

        return arrayList.toList()
    }

    @TypeConverter
    fun toListActivity(listString: List<String>): List<Activity> {
        val arrayList = arrayListOf<Activity>()
        listString.map {
            val items = it.split(";")
            Log.d("RRR", "$items")
            val cat = items[6].substring(1, items[6].length - 1)
            val categories: List<String> = cat.split(",")
            val activity = Activity(
                items[0],
                items[1],
                items[2],
                items[3],
                items[4],
                items[5].toDouble(),
                categories,
                items[7],
                items[8].toBoolean(),
                items[9].toBoolean(),
                items[10],
                items[11],
                items[12].toDouble(),
                items[13].toDouble()
            )

            arrayList.add(activity)
        }
        return arrayList.toList()
    }
}

class ListFlightsConverter {
    @TypeConverter
    fun fromListFlights(listFlight: List<Flight>): List<String> {
        val arrayList: ArrayList<String> = arrayListOf()
        listFlight.map {

            val string =
                "${it.id};${it.travelId};${it.SegmentId};${it.prixTotal};${it.prixParPassager};${it.dateDepart};${it.heureDepart};${it.dateArrivee};${it.heureArrivee};${it.dureeVol};${it.lieuDepart};${it.lieuArrivee};${it.carrierCode};${it.carrierCodeLogo};${it.carrierName};${it.nbEscales};${it.retour};${it.favoris};${it.uuid}"

            arrayList.add(string)

        }
        return arrayList.toList()
    }

    @TypeConverter
    fun toListFlights(listString: List<String>): List<Flight> {
        val arrayList: ArrayList<Flight> = arrayListOf()
        listString.map {
            val items = it.split(";")

            val flight = Flight(
                items[0].toInt(),
                items[1].toInt(),
                items[2].toInt(),
                items[3].toDouble(),
                items[4].toDouble(),
                items[5],
                items[6],
                items[7],
                items[8],
                items[9],
                items[10],
                items[11],
                items[12],
                items[13],
                items[14],
                items[15].toInt(),
                items[16].toInt(),
                items[17].toBoolean(),
                items[18]
            )
            arrayList.add(flight)
        }

        return arrayList.toList()
    }
}
class ListHotelsConverter {
    @TypeConverter
    fun fromListHotels(listHotels: List<Hotel>): List<String> {
        val arrayList: ArrayList<String> = arrayListOf()
        listHotels.map {

            val string =
                "${it.id};${it.hotelId};${it.hotelName};${it.hotelDescription};${it.rate};${it.image_url};${it.adresse};${it.telephone};${it.latitude};${it.longitude};${it.prix};${it.equipements};${it.listIdOffer};${it.favoris}"

            arrayList.add(string)

        }
        return arrayList.toList()
    }

    @TypeConverter
    fun toListHotels(listString: List<String>): List<Hotel> {
        val arrayList: ArrayList<Hotel> = arrayListOf()
        listString.map {
            val items = it.split(";")
            var cat = items[6].substring(1, items[6].length - 1)
            val adresse: MutableList<String> = cat.split(",") as MutableList<String>

            cat = items[11].substring(1, items[11].length - 1)
            val equipements: MutableList<String> = cat.split(",") as MutableList<String>

            cat = items[12].substring(1, items[12].length - 1)
            val offers: MutableList<String> = cat.split(",") as MutableList<String>

            var rate: Int? = null
            if(items[4] != null){
                rate = items[4]?.toInt()
            }
            val hotel = Hotel(
                items[0].toInt(),
                items[1],
                items[2],
                items[3],
                items[4]?.toInt(),
                items[5],
                adresse,
                items[7],
                items[8].toDouble(),
                items[9].toDouble(),
                items[10].toDouble(),
                equipements,
                offers,
                items[13].toBoolean()
            )
            arrayList.add(hotel)
        }

        return arrayList.toList()
    }
}


