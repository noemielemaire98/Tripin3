package com.example.tripin.trip

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Voyage
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_detail_voyage.*
import kotlinx.coroutines.runBlocking

class InfoVoyageFrangment : Fragment() {
    private var voyage: Voyage? = null
    private var voyageDao: VoyageDao? = null
    private lateinit var viewpager: ViewPager
    private lateinit var tabLayout: TabLayout

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val voyage = (activity as? DetailVoyage2)!!.voyage
        val view = inflater.inflate(R.layout.activity_infovoyage , container, false)
        val title = view.findViewById<TextView>(R.id.voyage_title_textview)
        val destination = view.findViewById<TextView>(R.id.voyage_destination_textview)
        val dateDepart = view.findViewById<TextView>(R.id.voyage_dateDepart_textview)
        val dateRetour = view.findViewById<TextView>(R.id.voyage_dateRetour_textview)
        val nbVoyageur = view.findViewById<TextView>(R.id.voyage_nb_voyageurs_textview)
        val budget = view.findViewById<TextView>(R.id.voyage_budget_textview)
        val nbActivité = view.findViewById<TextView>(R.id.voyage_activite_textview)
        val nbVol = view.findViewById<TextView>(R.id.voyage_flight_textview)
        val nbHotel = view.findViewById<TextView>(R.id.voyage_hotel_textview)
        val date = view.findViewById<TextView>(R.id.voyage_date_textview)


        title.text = "${voyage?.titre}"
        destination.text = "${voyage?.destination}"
        date.text = "Du ${voyage?.date} au ${voyage?.dateRetour}"
        nbVoyageur.text = "${voyage?.nb_voyageur}"
        budget.text = "${voyage?.budget} €"
        nbActivité.text ="${voyage?.list_activity?.size}"
        nbVol.text ="${voyage?.list_flights?.size}"
        nbHotel.text ="${voyage?.list_hotels?.size}"

//        Log.d("zzzz" , "id =$voyage")
//        title.text = "Titre : ${voyage?.titre}"
//        destination.text = "Destination : ${voyage?.destination}"
//        dateDepart.text = "Du ${voyage?.date}"
//        dateRetour.text = "Au ${voyage?.dateRetour}"
//        nbVoyageur.text = "Nombre de voyageur : ${voyage?.nb_voyageur}"
//        budget.text = "Budget : ${voyage?.budget} €"
//        nbActivité.text ="Nombre d'activités : ${voyage?.list_activity?.size}"
//        nbVol.text ="Nombre de vol : ${voyage?.list_flights?.size}"
//        nbHotel.text ="Nombre d'hotel : ${voyage?.list_hotels?.size}"

        return view
    }


}
