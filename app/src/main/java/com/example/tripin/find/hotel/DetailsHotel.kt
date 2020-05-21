package com.example.tripin.find.hotel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.shopping.HotelOffersByHotel
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.HotelDao
import com.example.tripin.data.OfferDao
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Hotel
import com.example.tripin.model.Offer
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.activity_details_hotel.*
import kotlinx.coroutines.runBlocking
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.createvoyage_popup.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailsHotel : AppCompatActivity() {

    private var hotel: Hotel? = null
    private var favoris: Boolean? = null
    private var id: Int = 0
    private var hotelDaoSaved: HotelDao? = null
    private var voyageDao: VoyageDao? = null
    private var offerDao: OfferDao? = null
    private var hotels_saved_bdd = emptyList<Hotel>()
    private var hotels_search_bdd = emptyList<Hotel>()
    private var offersList: MutableList<Offer>? = null
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    var date_debut = ""
    var date_fin = ""
    private var destination = ""
    private var budget = ""
    var image = ""


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


        //Ajout voyage
        fab_plus.setOnClickListener {
            Log.d("POP-UP", "Click")
            voyageDao = databasesaved.getVoyageDao()
            val list_voyage: ArrayList<String> = arrayListOf<String>()
            val list_checkedItems = ArrayList<Boolean>()
            val selectedList = ArrayList<Int>()
            // je récupère la liste des titres des dossiers de voyage et si oui ou non l'activité appartient à ce voyage
            runBlocking {
                val voyages = voyageDao?.getVoyage()
                if (voyages != null) {
                    voyages.map {
                        var deja_ajoute = false
                        list_voyage.add(it.titre!!)
                        it.list_hotels?.map {
                            if (it.id == hotel!!.id) {
                                deja_ajoute = true
                            }
                        }
                        list_checkedItems.add(deja_ajoute)
                    }
                }
            }
            // j'ouvre la pop-up
            val plusdialog = androidx.appcompat.app.AlertDialog.Builder(this)
            plusdialog.setTitle("Dossier de voyage")
            val list_choix = arrayListOf<String>()
            if (list_voyage.isEmpty()) {
                plusdialog.setMessage("Vous n'avez constitué aucun dossier de voyage, cliquez sur créer")
            } else {
                plusdialog.setMultiChoiceItems(
                    list_voyage.toTypedArray(),
                    list_checkedItems.toBooleanArray()
                ) { dialog, which: Int, isChecked ->
                    // Update the current focused item's checked status
                    list_checkedItems[which] = isChecked
                    if (isChecked) {
                        list_choix.add(list_voyage.get(which))
                        runBlocking {
                            val voyage = voyageDao?.getVoyageByTitre(list_voyage.get(which))
                            val ancienne_list = voyage!!.list_hotels?.toMutableList()
                            ancienne_list?.add(hotel!!)
                            val nouvelle_liste = ancienne_list?.toList()
                            voyage.list_hotels = nouvelle_liste

                            voyageDao?.updateVoyage(voyage)
                        }
                        Toast.makeText(
                            this,
                            "L'hôtel a bien été ajouté à ${list_voyage.get(which)}",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        list_choix.remove(list_voyage.get(which))
                        runBlocking {
                            val voyage = voyageDao?.getVoyageByTitre(list_voyage.get(which))
                            var ancienne_list = voyage?.list_hotels?.toMutableList()
                            ancienne_list?.remove(hotel!!)
                            val nouvelle_liste = ancienne_list?.toList()
                            voyage!!.list_hotels = nouvelle_liste
                            voyageDao?.updateVoyage(voyage!!)
                        }
                        Toast.makeText(
                            this,
                            "L'hôtel a bien été supprimé de ${list_voyage.get(which)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            plusdialog.setPositiveButton(android.R.string.ok) { _, _ -> }


            plusdialog.setNeutralButton("Créer") { _, _ ->

                val createdialog = androidx.appcompat.app.AlertDialog.Builder(this)
                val view = layoutInflater.inflate(R.layout.createvoyage_popup, null)
                val editText = view.findViewById<EditText>(R.id.et_date)
                val okbutton = view.findViewById<Button>(R.id.bt_ok)
                val returnbutton = view.findViewById<Button>(R.id.bt_retour)
                val editTitre = view.findViewById<EditText>(R.id.et_titre)
                val editDate = view.findViewById<EditText>(R.id.et_date)
                createdialog.setView(view)
                createdialog.setTitle("Créer")
                val alert = createdialog.show()
                editText.setOnClickListener {
                    rangeDatePickerPrimeCalendar(editText)
                }
                okbutton.setOnClickListener {
                    if (!editTitre.text.isEmpty() && !editDate.text.isEmpty()) {
                        val voyage = Voyage(
                            0,
                            view.et_titre.text.toString(),
                            date_debut,
                            date_fin,
                            image,
                            view.et_nb_voyageur.selectedItem.toString().toInt(),
                            emptyList(),
                            emptyList(),
                            emptyList(),
                            destination,
                            budget
                        )
                        runBlocking {
                            voyageDao?.addVoyage(voyage)
                        }
                        list_voyage.add(view.et_titre.text.toString())
                        list_checkedItems.add(false)
                        plusdialog.setMultiChoiceItems(
                            list_voyage.toTypedArray(),
                            list_checkedItems.toBooleanArray()
                        ) { dialog, which: Int, isChecked ->
                            // Update the current focused item's checked status
                            list_checkedItems[which] = isChecked
                            if (isChecked) {
                                list_choix.add(list_voyage.get(which))
                                runBlocking {
                                    val voyage = voyageDao?.getVoyageByTitre(list_voyage.get(which))
                                    val ancienne_list = voyage!!.list_hotels?.toMutableList()
                                    ancienne_list?.add(hotel!!)
                                    val nouvelle_liste = ancienne_list?.toList()
                                    voyage.list_hotels = nouvelle_liste
                                    voyageDao?.updateVoyage(voyage)
                                }
                                Toast.makeText(
                                    this,
                                    "L'hôtel a bien été ajouté à ${list_voyage.get(which)}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("RRI", "add")
                            } else {
                                list_choix.remove(list_voyage.get(which))
                                runBlocking {
                                    val voyage = voyageDao?.getVoyageByTitre(list_voyage.get(which))
                                    var ancienne_list = voyage?.list_hotels?.toMutableList()
                                    ancienne_list?.remove(hotel!!)

                                    val nouvelle_liste = ancienne_list?.toList()
                                    voyage!!.list_hotels = nouvelle_liste
                                    voyageDao?.updateVoyage(voyage!!)
                                }
                                Toast.makeText(
                                    this,
                                    "L'activité à bien été supprimée de ${list_voyage.get(which)}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        alert.dismiss()
                        plusdialog.show()
                    } else {
                        Toast.makeText(this, "Veuillez saisir tous les champs", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                returnbutton.setOnClickListener {
                    alert.dismiss()
                    plusdialog.show()
                }
            }

            plusdialog.show()
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




