package com.example.tripin.find.hotel

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.shopping.HotelOffersByHotel
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.data.OfferDao
import com.example.tripin.model.Hotel
import com.example.tripin.model.Offer
import kotlinx.android.synthetic.main.activity_details_hotel.*
import kotlinx.coroutines.runBlocking
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.file.Path

class DetailsHotel : AppCompatActivity() {

    private var hotel: Hotel? = null
    private var favoris: Boolean? = null
    private var id: Int = 0
    private var hotelDaoSaved: HotelDao? = null
    private var hotelDaoSearch: HotelDao? = null
    private var offerDao: OfferDao? = null
    private var hotels_saved_bdd = emptyList<Hotel>()
    private var hotels_search_bdd = emptyList<Hotel>()
    private var offersList: MutableList<Offer>? = null
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_hotel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        equipement_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        offers_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        hotel = intent.getParcelableExtra("hotel")
        favoris = intent.getBooleanExtra("favoris", false)

        val databaseoffers =
            Room.databaseBuilder(this, AppDatabase::class.java, "searchDatabase")
                .build()
        offerDao = databaseoffers.getOfferDao()


        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()
        hotelDaoSaved = databasesaved.getHotelDao()


        val amadeus = Amadeus
            .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
            .build()




        GlobalScope.launch {
            offerDao!!.deleteOffers()

            val hotelId = hotel!!.hotelId
            var offer: String? = null

            Log.d("resultOffer", hotel!!.listIdOffer.toString())
            val resultOffers = amadeus.shopping.hotelOffersByHotel[Params.with("hotelId", hotelId)]






            runOnUiThread(java.lang.Runnable {
                resultOffers.offers.map {

                    Log.d("price", it.price.total)
                    var type: String
                    if (it.type == null) {
                        type = ""
                    } else {
                        type = it.type
                    }
                    val offer = Offer(
                        it.id,
                        type,
                        it.room.typeEstimated.beds,
                        it.room.typeEstimated.bedType,
                        it.room.description.text,
                        it.price.total.toDouble()
                    )

                    runBlocking {
                        if (offer.bed_Type == null || offer.nb_bed == null) {
                        } else {
                            offerDao!!.addOffers(offer)
                        }
                    }
                }
                var offers_bdd: List<Offer> = emptyList()
                runBlocking {
                    offers_bdd = offerDao!!.getOffers()

                }
                offers_recyclerview.adapter = OffersAdapter(offers_bdd)


            })


        }



        runBlocking {
            hotels_saved_bdd = hotelDaoSaved!!.getHotels()
        }

        hotels_saved_bdd?.forEach {
            if (it.hotelId == hotel!!.hotelId) {
                favoris = true
            }
        }
        if (favoris == true) {
            fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)


        } else if (favoris == false) {
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

        when (hotel?.rate) {
            1 -> one_star_layout.visibility = View.VISIBLE
            2 -> two_star_layout.visibility = View.VISIBLE
            3 -> three_star_layout.visibility = View.VISIBLE
            4 -> four_star_layout.visibility = View.VISIBLE
            5 -> five_star_layout.visibility = View.VISIBLE
            else -> {
            }
        }


        var nom = "${hotel?.hotelName}".toLowerCase()
        detail_hotel_nom_textview.text = formatString(nom)
        detail_hotel_description_textview.text = hotel?.hotelDescription
        detail_hotel_adresse_texview.setTypeface(null, Typeface.ITALIC)
        detail_hotel_telephone_texview.setTypeface(null, Typeface.ITALIC)
        detail_hotel_adresse_texview.text =
            "${hotel?.adresse?.get(0)} ${hotel?.adresse?.get(1)}, ${hotel?.adresse?.get(2)}, ${hotel?.adresse?.get(
                3
            )}, ${hotel?.adresse?.get(4)}"
        detail_hotel_telephone_texview.text = "Téléphone : ${hotel?.telephone}"
        equipement_recyclerview.adapter =
            EquipementAdapter(
                hotel?.equipements,
                this@DetailsHotel
            )






        fab_fav.setOnClickListener {
            if (favoris == false) {
                runBlocking {
                    hotelDaoSaved?.addHotel(hotel!!)
                }
                favoris = true
                fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)
                Toast.makeText(
                    this@DetailsHotel,
                    "L'hôtel a bien été ajoutée aux favoris",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (favoris == true) {
                runBlocking {
                    hotelDaoSaved?.deleteHotel(hotel!!.id)
                }
                favoris = false
                fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                Toast.makeText(
                    this@DetailsHotel,
                    "L'hôtel bien été supprimé des favoris",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        button_description.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Lire plus")
                setMessage("${hotel?.hotelDescription}")
                setPositiveButton(android.R.string.ok) { _, _ ->
                }
                show()
            }
        }



        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            val location = LatLng(hotel!!.latitude, hotel!!.longitude)
            googleMap.addMarker(MarkerOptions().position(location).title("Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))

        }
    }


    fun formatString(nom: String): String {
        var stringFormat: String = nom
        val nomSplit = nom.split(" ")
        for (item in nomSplit) {
            val ajout = item.capitalize()
            if (stringFormat == nom) {
                stringFormat = ajout
            } else {
                stringFormat = "$stringFormat $ajout"
            }
        }
        return stringFormat
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

