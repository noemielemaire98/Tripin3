package com.example.tripin.find.hotel

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.model.Rooms
import kotlinx.android.synthetic.main.activity_details_room.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class DetailsRoom : AppCompatActivity() {

    private lateinit var room: Rooms
    private var url : String? = ""

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_room)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        room = intent.getParcelableExtra("room")
        Log.d("ROOMCCC", room.toString())


       Glide.with(this)
            .load(room.imagesRoom)
            .centerCrop()
            .into(detail_hotel_imageview)

        var description = descriptionFixed(room.descriptionRoom)
        detail_room_nom_textview.text = room.nameRoom
        detail_room_description_textview.text = description


        if(room.promo!= null){
            promo_layout.visibility = View.VISIBLE
            promo_room.text = room.promo
        }

        details_occupancy_room.text = room.occupancyRoom


        if(room.listOccupants?.size != 0){
            price_room.text ="${room.price}"
            val checkIn =  SimpleDateFormat("yyyy-MM-dd").parse(room.checkIn)
            val checkOut =  SimpleDateFormat("yyyy-MM-dd").parse(room.checkOut)
            val diff: Long = checkOut.time - checkIn.time
            val nbNuits = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
            price_night.text = "${room.priceNight}"

            date_room_details.text = "${room.checkIn} au ${room.checkOut}"
            nb_night_details.text = "$nbNuits nuit(s)"
        }


        when (room.listOccupants?.size) {
            1 -> {
                    url ="https://fr.hotels.com/dl/hotel/details.html?hotelId=${room.idHotel}&q-check-in=${room.checkIn}&q-check-out=${room.checkOut}&q-rooms=1&q-room-0-adults=${room.listOccupants?.get(0)}&q-room-0-children=0"
            }
            2 ->{
                url ="https://fr.hotels.com/dl/hotel/details.html?hotelId=${room.idHotel}&q-check-in=${room.checkIn}&q-check-out=${room.checkOut}&q-rooms=2&q-room-0-adults=${room.listOccupants?.get(0)}&q-room-0-children=0&q-room-1-adults=${room.listOccupants?.get(1)}&q-room-1-children=0"
        }
            3 -> {
                url ="https://fr.hotels.com/dl/hotel/details.html?hotelId=${room.idHotel}&q-check-in=${room.checkIn}&q-check-out=${room.checkOut}&q-rooms=3&q-room-0-adults=${room.listOccupants?.get(0)}&q-room-0-children=0&q-room-1-adults=${room.listOccupants?.get(1)}&q-room-1-children=0&q-room-2-adults=${room.listOccupants?.get(1)}&q-room-2-children=0"
            }
            4 -> {
                url ="https://fr.hotels.com/dl/hotel/details.html?hotelId=${room.idHotel}&q-check-in=${room.checkIn}&q-check-out=${room.checkOut}&q-rooms=4&q-room-0-adults=${room.listOccupants?.get(0)}&q-room-0-children=0&q-room-1-adults=${room.listOccupants?.get(1)}&q-room-1-children=0&q-room-2-adults=${room.listOccupants?.get(1)}&q-room-2-children=0&q-room-3-adults=${room.listOccupants?.get(3)}&q-room-3-children=0"
            }
            else -> {
                room_price_cardview.visibility = View.GONE
                booking_button.visibility = View.GONE
            }
        }





        booking_button.setOnClickListener {
          if(url!=""){
              val uri: Uri = Uri.parse(url)
              val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
              if (intent.resolveActivity(packageManager) != null) {
                  startActivity(intent)
              }
        } else{
              Toast.makeText(this, "Pas d'occupant", Toast.LENGTH_SHORT).show()
          }

        }


    }

    //TODO : description moche
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