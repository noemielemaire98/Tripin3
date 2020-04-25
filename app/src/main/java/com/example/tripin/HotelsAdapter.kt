package com.example.tripin


import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tripin.model.Hotel
import kotlinx.android.synthetic.main.hotel_view.view.*

class HotelsAdapter (val hotels : List<Hotel>): RecyclerView.Adapter<HotelsAdapter.HotelViewHolder>(){

    class HotelViewHolder(val hotelView : View):RecyclerView.ViewHolder(hotelView)

    override fun getItemCount(): Int = hotels.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.hotel_view, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = hotels[position]

        holder.hotelView.hotel_ville_textview.setTypeface(null, Typeface.ITALIC)
        holder.hotelView.hotel_ville_textview.text="Paris".toUpperCase()

        //Definition du nom
        var nom = "${hotel.hotelName}".toLowerCase()
        //TODO : Essayer de mettre la premiere lettre de chaque mot en masjucule
        holder.hotelView.hotel_name_textview.setTypeface(null, Typeface.BOLD)
        holder.hotelView.hotel_name_textview.text= formatNomHotel(nom)

        //Definition de l'adresse
        var adresse = "${hotel.adresse}".toLowerCase()
        holder.hotelView.hotel_adresse_textview.setTypeface(null, Typeface.ITALIC)
        holder.hotelView.hotel_adresse_textview.text = formatNomHotel(adresse)

        //Definition de la note
        val note :String
        if(hotel.rate == null){
            note = "Non noté"
        }else
        { note = hotel.rate.toString()}
            holder.hotelView.hotel_rate_textview.text=note


        //Récupération de l'image
        // TODO : Vérifier si les images "test" se mettent à jour
            holder.hotelView.hotels_imageview.setImageResource(R.drawable.hotel)

       // }else{
       // Glide.with(holder.hotelView)
        //   .load(hotel.image_url)
         //  .centerCrop()
         //   .into(holder.hotelView.hotels_imageview)}


        holder.hotelView.setOnClickListener {
            val intent= Intent(it.context, DetailsHotel::class.java)
            intent.putExtra("id",hotel.id)
            it.context.startActivity(intent)
        }
    }

    fun formatNomHotel(nom:String): String {

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




}
