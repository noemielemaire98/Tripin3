package com.example.tripin.find.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.ActivityDao
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Activity
import com.example.tripin.model.Voyage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_detail_activites.*
import kotlinx.android.synthetic.main.createvoyage_popup.*
import kotlinx.android.synthetic.main.createvoyage_popup.view.*
import kotlinx.android.synthetic.main.fragment_find_flight2.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DetailActivites : AppCompatActivity() {

    private var activite: Activity? = null
    private var activityDaoSaved: ActivityDao? = null
    private var voyageDao: VoyageDao? = null
    private var favoris : Boolean = false
    private var list_activities_bdd = emptyList<Activity>()
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var googleMap: GoogleMap
    var date_debut =""
    var date_fin=""



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_activites)


        // BOUTON RETOUR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ON RECUPERE L'ACTIVITE CHOISIT DANS LA RECYCLERVIEW
       activite = intent.getParcelableExtra("activity")
       favoris = intent.getBooleanExtra("attribut_fav",false)


        // ON APPELLE LA BDD
        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()
        activityDaoSaved = databasesaved.getActivityDao()


        // ON SET LES ATTRIBUTS
            val url = activite?.cover_image_url
            Glide.with(this@DetailActivites)
                .load(url)
                .into(detail_activity_imageview)
        runBlocking {
            list_activities_bdd = activityDaoSaved!!.getActivity()
        }
        list_activities_bdd.forEach {
            if (it.title == activite!!.title) {
                favoris = true
            }
        }

        if(favoris == true){
            fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)

        }else if (favoris == false){
            fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)

        }

        if(activite?.top_seller == true){
            layout_activity_topseller.visibility = View.VISIBLE
        }
        if(activite?.must_see == true){
            layout_activity_must_see.visibility = View.VISIBLE
        }

        if(activite?.reviews_avg != 0.0){
            layout_note_activity.visibility = View.VISIBLE
            tv_activity_rate.text = "${activite?.reviews_avg}"
        }

            detail_activity_titre_textview.text = activite?.title
            detail_activity_dispo_textview.text = "Dispo : " + activite?.operational_days
            detail_activity_prix_textview.text = "Prix : " + activite?.formatted_iso_value
            detail_activity_about_textview.text = activite?.description
            rv_categories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rv_categories.adapter = CategoryAdapter(activite!!.category)


        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            val location = LatLng(activite!!.latitude,activite!!.longitude)
            googleMap.addMarker(MarkerOptions().position(location).title("Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,12f))

        }



        // 3 - AU CLIC SUR LE BOUTON FAVORIS
        fab_fav.setOnClickListener {
            if (favoris == false) {
                runBlocking {
                        activityDaoSaved?.addActivity(activite!!)
                }
                favoris = true
                fab_fav.setImageResource(R.drawable.ic_favorite_black_24dp)
                Toast.makeText(this, "L'activité a bien été ajoutée aux favoris", Toast.LENGTH_SHORT).show()

            } else if (favoris == true) {

                runBlocking {
                activityDaoSaved?.deleteActivity(activite!!.uuid)
                }
                favoris = false
                fab_fav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                Toast.makeText(this, "L'activité a bien été supprimé des favoris", Toast.LENGTH_SHORT).show()
            }
        }


        // AU CLIC SUR LE BOUTON RESERVER
        booking_button.setOnClickListener {
            val u = ((activite?.url)?.split("sandbox."))?.get(1)
            val urll = "https://$u"
            val uri : Uri = Uri.parse(urll)
            val intent : Intent = Intent(Intent.ACTION_VIEW,uri)
            if(intent.resolveActivity(packageManager) != null){
                startActivity(intent)
            }

        }


        //AU CLIC SUR LE BOUTON AJOUT A UN VOYAGE
        fab_plus.setOnClickListener {
            voyageDao = databasesaved.getVoyageDao()
            var list_voyage: Array<String> = arrayOf<String>()
            val list_voyage2: ArrayList<String> = arrayListOf<String>()
            val selectedList = ArrayList<Int>()
            runBlocking {
                if(voyageDao?.getVoyage() != null){
                    voyageDao?.getVoyage()!!.map {list_voyage2.add(it.titre!!) }
                    list_voyage = list_voyage2.toTypedArray()
                }
            }
            val plusdialog = AlertDialog.Builder(this)
            //AlertDialog.Builder(this).apply {
                   plusdialog.setTitle("Dossier de voyage")
                   val list_choix = arrayListOf<String>()
                   if(list_voyage.isEmpty()){
                       plusdialog.setMessage("Vous n'avez constitué aucun dossier de voyage, cliquez sur ajouter")
                   }else{
                       plusdialog.setMultiChoiceItems(list_voyage,null){ dialog, which: Int, isChecked ->
                           // Update the current focused item's checked status
                           if (isChecked) {
                               selectedList.add(which)
                               list_choix.add(list_voyage.get(which))
                           } else if (selectedList.contains(which)) {
                               selectedList.remove(Integer.valueOf(which))
                               list_choix.remove(list_voyage.get(which))
                           }
                       }
                   }
                  plusdialog.setPositiveButton(android.R.string.ok) { _, _ ->
                       if(!list_choix.isEmpty()){
                           list_choix.forEach {
                               runBlocking {
                                   val voyage = voyageDao?.getVoyageByTitre(it)
                                   val ancienne_list = voyage!!.list_activity?.toMutableList()
                                   ancienne_list?.add(activite!!)
                                   val nouvelle_liste = ancienne_list?.toList()
                                   voyage.list_activity = nouvelle_liste
                                   voyageDao?.updateVoyage(voyage)
                               }
                         }
                           Toast.makeText(this,"L'activité à bien été ajoutée",Toast.LENGTH_SHORT).show()
                       }
                   }

                plusdialog.setNeutralButton("Créer"){_, _ ->

                       AlertDialog.Builder(this).apply {
                           val view = layoutInflater.inflate(R.layout.createvoyage_popup,null)
                           var editText = view.findViewById<EditText>(R.id.et_date)
                           setView(view)
                           setTitle("Créer")
                           editText.setOnClickListener {
                               rangeDatePickerPrimeCalendar(editText)
                           }

                           setPositiveButton("OK"){_,_ ->
                               val voyage = Voyage(0,view.et_titre.text.toString(),date_debut,date_fin,R.drawable.destination1,view.et_nb_voyageur.text.toString().toInt(), emptyList())
                               runBlocking {
                                   voyageDao?.addVoyage(voyage)
                               }
                               list_voyage2.add(view.et_titre.text.toString())
                               list_voyage = list_voyage2.toTypedArray()
                               plusdialog.setMultiChoiceItems(list_voyage,null){ dialog, which: Int, isChecked ->
                                   // Update the current focused item's checked status
                                   if (isChecked) {
                                       selectedList.add(which)
                                       list_choix.add(list_voyage.get(which))
                                   } else if (selectedList.contains(which)) {
                                       selectedList.remove(Integer.valueOf(which))
                                       list_choix.remove(list_voyage.get(which))
                                   }
                               }
                               Toast.makeText(this.context,"Le dossier ${voyage.titre}  a bien été ajouté",Toast.LENGTH_SHORT).show()
                               plusdialog.show()
                           }
                           setNeutralButton("Retour"){_,_ ->
                               plusdialog.show()
                           }
                           show()
                       }
                   }

                    plusdialog.show()
               //}


        }

        lire_plus_activity.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Lire plus")
                setMessage("${activite?.about}")
                setPositiveButton(android.R.string.ok) { _, _ ->
                }
                show()
            }
        }
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


    @SuppressLint("SetTextI18n")
    private fun rangeDatePickerPrimeCalendar(editText: EditText) {
        val rangeDaysPickCallback = RangeDaysPickCallback { startDate, endDate ->
            // TODO
            Log.d("Date", "${startDate.shortDateString} ${endDate.shortDateString}")
            val parser =
                SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val formatterDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedStartDate =
                formatterDate.format(parser.parse(startDate.shortDateString)!!)
            val parsedEndDate =
                formatterDate.format(parser.parse(endDate.shortDateString)!!)
            editText.setText("Du $parsedStartDate au $parsedEndDate")
            date_debut = parsedStartDate
            date_fin = parsedEndDate
        }

        val today = CivilCalendar()

            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .firstDayOfWeek(Calendar.MONDAY)
                .minPossibleDate(today)
                .build()

            datePickerT.show(supportFragmentManager, "PrimeDatePickerBottomSheet")


    }
}

