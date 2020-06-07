package com.example.tripin.find.hotel

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Hotel
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.hotel_trip_view.view.*
import kotlinx.coroutines.runBlocking

class HotelAdapterTrip(val list_hotels: MutableList<Hotel>,  var voyage: Voyage,val favoris : ArrayList<Boolean>, val layout_nohotel : RelativeLayout) : RecyclerView.Adapter<HotelAdapterTrip.HotelViewHolder>()  {

    class HotelViewHolder(val hotelView : View) : RecyclerView.ViewHolder(hotelView)

    private var voyageDao : VoyageDao? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.hotel_trip_view, parent, false)
        val databasesaved =
            Room.databaseBuilder(parent.context, AppDatabase::class.java, "savedDatabase")
                .build()

        voyageDao = databasesaved.getVoyageDao()

        context = parent.context

        return HotelViewHolder(
            view
        )
    }

    override fun getItemCount(): Int = list_hotels.size


    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {

        val hotel = list_hotels[position]
        var nom = "${hotel.hotelName}".toLowerCase()
        holder.hotelView.hotel_name_textview.setTypeface(null, Typeface.BOLD)
        holder.hotelView.hotel_name_textview.text= formatNomHotel(nom)


        //Definition de l'adresse

        var adresse = mutableListOf<String>()
        hotel.adresse.forEach {
            if(it == "null"){
                adresse.add("NC")
            }else{
                adresse.add(it)
            }
        }
        holder.hotelView.hotel_adresse1_textview.setTypeface(null, Typeface.ITALIC)
        holder.hotelView.hotel_adresse1_textview.text = "${adresse[0]}, ${adresse[1]} "
        holder.hotelView.hotel_adresse2_textview.setTypeface(null, Typeface.ITALIC)
        holder.hotelView.hotel_adresse2_textview.text = "${adresse[2]}, ${adresse[3]?.toUpperCase()}"

        //Definition de la note

        if(hotel.rate == null){
        }else
        { holder.hotelView.layout_hotel_rate.visibility = View.VISIBLE
            holder.hotelView.hotel_rate_textview.text = hotel.rate.toString()

        }


        holder.hotelView.hotel_prix_textview.text = "A partir de : ${hotel.prix}"


        //Récupération de l'image
        if(hotel.image_url==null) {
            holder.hotelView.hotels_imageview.setImageResource(R.drawable.hotel)
        }
        else{
            Glide.with(holder.hotelView)
                .load(hotel.image_url)
                .centerCrop()
                .into(holder.hotelView.hotels_imageview)}


        holder.hotelView.bt_delete.setOnClickListener {
            list_hotels.removeAt(position)
            voyage.list_hotels= list_hotels.toList()
            runBlocking {
                voyageDao?.updateVoyage(voyage)
            }
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,list_hotels.size)

            if(list_hotels.isEmpty()){
                layout_nohotel.visibility = View.VISIBLE
            }
        }


        holder.hotelView.setOnClickListener {
            val intent= Intent(it.context, DetailsHotel::class.java)
            intent.putExtra("hotel", hotel)
            intent.putExtra("favoris", favoris[position])
            intent.putExtra("listAdults", " ")
            it.context.startActivity(intent)
        }




    }

    fun formatNomHotel(nom:String): String {
        var nomFormat:String = nom
        val nomSplit = nom.split(" ")
        for(item in nomSplit){
            val mot = item.capitalize()
            if(nomFormat == nom){
                nomFormat = mot
            }else{
                nomFormat = "$nomFormat $mot"
            }}
        return nomFormat

    }

}