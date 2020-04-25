package com.example.tripin

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.model.Hotel
import kotlinx.android.synthetic.main.activity_details_hotel.*
import kotlinx.android.synthetic.main.hotel_view.view.*
import kotlinx.coroutines.runBlocking

class DetailsHotel : AppCompatActivity() {

    private var hotel: Hotel? = null
    private var id: Int = 0
    private var hotelDao: HotelDao? = null
    private var favoris: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_hotel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getIntExtra("id", 0)

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "allhotels")
                .build()

        hotelDao = database.getHotelDao()
        runBlocking {
            hotel = hotelDao!!.getHotel(id)
           // Log.d("image uri", hotel?.image_url)
  //          if ((hotel?.image_url==null) || (hotel?.image_url== " http://uat.multimediarepository.testing.amadeus.com/cmr/retrieve/hotel/EB874AAD4E0C410EB6D3C6841C85522B")){
                detail_hotel_imageview.setImageResource(R.drawable.hotel)

    /*        }else{
                Glide.with(this@DetailsHotel)
                    .load(hotel?.image_url)
                    .centerCrop()
                    .into(detail_hotel_imageview)}*/
            detail_hotel_imageview.setImageResource(R.drawable.hotel)


            var nom = "${hotel?.hotelName}".toLowerCase()
            detail_hotel_nom_textview.text=formatString(nom)
            detail_hotel_description_textview.text = hotel?.hotelDescription
            detail_hotel_adresse_texview.text= hotel?.adresse
            detail_hotel_email_texview.text=hotel?.email
            detail_hotel_telephone_texview.text = hotel?.telephone


            if(hotel?.favoris == true){
                favoris = true
                fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)


            }else {
                favoris=false
                fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)

            }


            // 3 - AU CLIC SUR LE BOUTON
            fab_fav.setOnClickListener {
                if (favoris == false) {
                    runBlocking {
                        hotelDao?.updateHotelFavoris(true, hotel!!.id)
                    }
                    favoris = true
                    fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)
                    Toast.makeText(this@DetailsHotel, "L'activité a bien été ajoutée aux favoris", Toast.LENGTH_SHORT).show()

                } else if (favoris == true) {
                    runBlocking {
                        hotelDao?.updateHotelFavoris(false, hotel!!.id)

                    }
                    favoris = false
                    fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    Toast.makeText(this@DetailsHotel, "L'activité a bien été supprimé des favoris", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    fun formatString(nom:String): String {

        Log.d("nom", nom)
        var nomFormat:String = nom
        val nomSplit = nom.split(" ")
        Log.d("nom split", nomSplit.toString())
        for(item in nomSplit){
            val mot = item.capitalize()
            if(nomFormat == nom){
                nomFormat = mot
            }else{
                nomFormat = "$nomFormat $mot"
            }}
        return nomFormat

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
