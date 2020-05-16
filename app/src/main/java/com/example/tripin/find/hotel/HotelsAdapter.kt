package com.example.tripin.find.hotel


import  android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.model.Hotel
import kotlinx.android.synthetic.main.hotel_view.view.*
import kotlinx.coroutines.runBlocking

class HotelsAdapter (val hotels : List<Hotel> , val favoris : ArrayList<Boolean>): RecyclerView.Adapter<HotelsAdapter.HotelViewHolder>(){

    class HotelViewHolder(val hotelView : View):RecyclerView.ViewHolder(hotelView)

    private var hotelDaoSearch : HotelDao ? = null
    private var hotelDaoSaved : HotelDao ? = null
    private lateinit var context: Context



    override fun getItemCount(): Int = hotels.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.hotel_view, parent, false)

        val databasesearch =
            Room.databaseBuilder(parent.context, AppDatabase::class.java, "searchDatabaseHotels")
                .build()
        val databasesaved =
        Room.databaseBuilder(parent.context, AppDatabase::class.java, "savedDatabaseHotels")
            .build()


        hotelDaoSearch = databasesearch.getHotelDao()
        hotelDaoSaved = databasesaved.getHotelDao()


        context = parent.context

        return HotelViewHolder(view)

    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {

        val hotel = hotels[position]

        //Definition du nom
        var nom = "${hotel.hotelName}".toLowerCase()
        holder.hotelView.hotel_name_textview.setTypeface(null, Typeface.BOLD)
        holder.hotelView.hotel_name_textview.text= formatNomHotel(nom)

        //Definition de l'adresse
        holder.hotelView.hotel_adresse1_textview.setTypeface(null, Typeface.ITALIC)
        holder.hotelView.hotel_adresse1_textview.text = "${hotel.adresse[0]}, ${hotel.adresse[1]} "
        holder.hotelView.hotel_adresse2_textview.setTypeface(null, Typeface.ITALIC)
        holder.hotelView.hotel_adresse2_textview.text = "${hotel.adresse[2]}, ${hotel.adresse[3]} "
        holder.hotelView.hotel_adresse3_textview.setTypeface(null, Typeface.ITALIC)
        holder.hotelView.hotel_adresse3_textview.text = hotel.adresse[4].toUpperCase()

        //Definition de la note

        if(hotel.rate == null){
        }else
        { holder.hotelView.layout_hotel_rate.visibility = View.VISIBLE
            holder.hotelView.hotel_rate_textview.text = hotel.rate.toString()

        }


        holder.hotelView.hotel_prix_textview.text = "A partir de : ${hotel.prix} €"


        //Récupération de l'image
        // TODO : Vérifier si les images "test" se mettent à jour
            holder.hotelView.hotels_imageview.setImageResource(R.drawable.hotel)

       // }else{
       // Glide.with(holder.hotelView)
        //   .load(hotel.image_url)
         //  .centerCrop()
         //   .into(holder.hotelView.hotels_imageview)}


        if (favoris[position] == true){
            holder.hotelView.fab_favActivity.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            holder.hotelView.fab_favActivity.setImageResource((R.drawable.ic_favorite_border_black_24dp))
        }


        holder.hotelView.setOnClickListener {
            val intent= Intent(it.context, DetailsHotel::class.java)
            intent.putExtra("hotel", hotel)
            intent.putExtra("favoris", favoris[position])
            it.context.startActivity(intent)
        }


        holder.hotelView.fab_favActivity.setOnClickListener {
            if(favoris[position] == true){
                holder.hotelView.fab_favActivity.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                runBlocking {
                    hotelDaoSaved?.deleteHotel(hotel.id)
                }
                favoris[position] = false
                Toast.makeText(context, "L'hôtel a bien été supprimé des favoris", Toast.LENGTH_SHORT).show()

            }else {
                holder.hotelView.fab_favActivity.setImageResource(R.drawable.ic_favorite_black_24dp)
                hotel.favoris= true
                runBlocking {
                    hotelDaoSaved?.addHotel(hotel)
                }
                favoris[position] = true
                Toast.makeText(context, "L'hôtel a bien été ajoutée aux favoris", Toast.LENGTH_SHORT).show()
            }
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
