package com.example.tripin.find.hotel

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.model.Hotel
import kotlinx.android.synthetic.main.activity_details_hotel.*
import kotlinx.coroutines.runBlocking

class DetailsHotel : AppCompatActivity() {

    private var hotel: Hotel? = null
    private var id: Int = 0
    private var hotelDao: HotelDao? = null
    private var favoris: Boolean = false
    private var bdd_hotels = emptyList<Hotel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_hotel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        equipement_recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        hotel = intent.getParcelableExtra("hotel")

        val database =
            Room.databaseBuilder(this, AppDatabase::class.java, "allhotels")
                .build()

        hotelDao = database.getHotelDao()


        runBlocking {
            bdd_hotels = hotelDao!!.getHotels()
            bdd_hotels?.forEach{
                if(it.hotelId==hotel!!.hotelId){
                    favoris = true
                }
            }
            if(hotel?.favoris == true || favoris == true){
                favoris = true
                fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)


            }else if(hotel?.favoris == false || favoris == false) {
                favoris=false
                fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)

            }


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
            var adresse = "${hotel?.adresse}".toLowerCase()
            detail_hotel_adresse_texview.text= formatString(adresse)
            detail_hotel_email_texview.text=hotel?.email
            detail_hotel_telephone_texview.text = hotel?.telephone
            equipement_recyclerview.adapter=
                EquipementAdapter(
                    hotel?.equipements,
                    this@DetailsHotel
                )






            fab_fav.setOnClickListener {
                if (favoris == false) {
                    hotel?.favoris=true
                    runBlocking {
                        hotelDao?.addHotel(hotel!!)
                    }
                    favoris = true
                    fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)
                    Toast.makeText(this@DetailsHotel, "L'hôtel a bien été ajoutée aux favoris", Toast.LENGTH_SHORT).show()

                } else if (favoris == true) {
                    hotel?.favoris = false
                    runBlocking {
                        hotelDao?.deleteHotel(hotel!!.id)
                    }
                    favoris = false
                    fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    Toast.makeText(this@DetailsHotel, "L'hôtel bien été supprimé des favoris", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    fun formatString(nom:String): String {
        var stringFormat:String = nom
        val nomSplit = nom.split(" ")
        for(item in nomSplit){
            val ajout = item.capitalize()
            if(stringFormat == nom){
                stringFormat = ajout
            }else{
                stringFormat = "$stringFormat $ajout"
            }}
        return stringFormat
    }


    override fun onResume() {
        super.onResume()
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
