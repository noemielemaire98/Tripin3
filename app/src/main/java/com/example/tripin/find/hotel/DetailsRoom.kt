package com.example.tripin.find.hotel

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.model.Rooms
import kotlinx.android.synthetic.main.activity_details_room.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class DetailsRoom : AppCompatActivity() {

    private lateinit var room: Rooms

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_room)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        room = intent.getParcelableExtra("room")

        Log.d("ROOM", room.imagesRoom.toString())

       /* Glide.with(this)
            .load(room.imagesRoom[0])
            .centerCrop()
            .into(detail_hotel_imageview)*/

        var description = descriptionFixed(room.descriptionRoom)
        detail_room_nom_textview.text = room.nameRoom
        detail_room_description_textview.text = description


        price_room.text ="Total : ${room.price}"

        val checkIn =  SimpleDateFormat("yyyy-MM-dd").parse(room.checkIn)
        val checkOut =  SimpleDateFormat("yyyy-MM-dd").parse(room.checkOut)
        val diff: Long = checkOut.time - checkIn.time
        val nbNuits = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        price_night.text = "${room.priceNight} pour ${nbNuits} nuit(s)"


        val url =  "https://fr.hotels.com/dl/hotel/details.html?hotelId=${room.idHotel}&q-check-in=${room.checkIn}&q-check-out=${room.checkOut}&q-rooms=1&q-room-0-adults=1&q-room-0-children=0"

        booking_button.setOnClickListener {
            val uri: Uri = Uri.parse(url)
            val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }

        }



        Log.d("URL", url)
    }


    private fun descriptionFixed(description : String) : String
    {

        var descriptionfixed = description.replace("</strong>", "", false)
        descriptionfixed = descriptionfixed.replace("<strong>", "", false)

        descriptionfixed = descriptionfixed.replace("</p>","\n", false)
        descriptionfixed = descriptionfixed.replace("<p>","", false)

        descriptionfixed = descriptionfixed.replace("<br/>","", false)

        descriptionfixed = descriptionfixed.replace("<b>"," \n", false)
        descriptionfixed = descriptionfixed.replace("</b>"," :\n ", false)

        return descriptionfixed
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
}