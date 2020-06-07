package com.example.tripin.find.hotel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.shopping.HotelOffersByHotel
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.bumptech.glide.Glide
import com.example.tripin.R
import com.example.tripin.data.*
import com.example.tripin.model.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DetailsHotel : AppCompatActivity() {

    private lateinit var citydao: CityDao
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
    private val service = retrofitHotel().create(HotelAPI::class.java)
    private val hotelKey = "5f672e716bmsh702ca7444dd484cp121785jsn039c3a4937f8"
    private var listEquipements : MutableList<Equipement> = mutableListOf()
    private var drawableNameList : MutableList<String> = mutableListOf()
    private var listProche : String = ""
    private var listRooms : MutableList<Rooms> = mutableListOf()
    private var listAdults : MutableList<String> = mutableListOf()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_hotel)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        equipement_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        offers_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        hotel = intent.getParcelableExtra("hotel")
        favoris = intent.getBooleanExtra("favoris", false)
        var Adults = intent.getStringExtra("listAdults")
        val dateArrivee = intent.getStringExtra("dateArrivee")
        val dateDepart = intent.getStringExtra("dateDepart")
        if(Adults!="[]"){
            initializeAdultsList(Adults)
        }
        listEquipements.clear()
        drawableNameList.clear()

        Log.d("zzz", "hotel = $hotel")

        val databasesaved =
            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                .build()
        hotelDaoSaved = databasesaved.getHotelDao()

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


        runBlocking {
            val result = service.getHotelDetail(hotel!!.hotelId.toInt(),"2020-08-01","2020-08-03","fr_FR","EUR", hotelKey)
Log.d("Test", result.toString())

            result.data.body.overview.overviewSections.forEach {
                if (it.type == "HOTEL_FEATURE"){
                    var description = it.content.toString().substringAfter("[")
                    description = description.substringBefore("]")
                    content_hotel_textview.text = "${description}."
                }
                else if (it.type == "LOCATION_SECTION"){
                   val proche = it.content
                    it.content.forEach{
                        listProche += "${it} \n"
                    }
                }
            }

            proche_hotel_textview.text = listProche



            result.data.body.amenities.forEach {

                var headingTop = it.heading
                it.listItems.forEach {
                    var  heading = it.heading
                    var listItems = it.listItems

                    val equipement = Equipement (
                        heading,
                        listItems
                    )
                    listEquipements.add(equipement)
                }

            }
            var amenitiesRoom : MutableList<String> = mutableListOf()
            if( result.data.body.roomsAndRates!=null){
            result.data.body.roomsAndRates.rooms?.forEach {
                var nameRoom = it.name
                var image:String? = it.images?.get(0)?.fullSizeUrl
                var descriptionRoom = it.additionalInfo.description
                var occupancyRoom:String?  = "${it.maxOccupancy.messageTotal} ${it.maxOccupancy.messageChildren}"
                amenitiesRoom = it.additionalInfo.details.amenities as MutableList<String>
                var priceNight  = it.ratePlans[0].price.nightlyPriceBreakdown.additionalColumns[0].value
                var price = it.ratePlans[0].price.current
                var promo : String? = it.ratePlans[0].welcomeRewards.info

                val room = Rooms(
                    hotel!!.hotelId,
                    nameRoom,
                    image,
                    descriptionRoom,
                    occupancyRoom,
                    amenitiesRoom,
                    priceNight,
                    price,
                    promo,
                    dateArrivee,
                    dateDepart,
                    listAdults
                    )

                listRooms.add(room)
Log.d("Price", room.toString())


            }}
            val equipementCsv = resources.openRawResource(R.raw.equipements)
            val listCvs = csvReader().readAll(equipementCsv)
            var drawableName = ""

            listEquipements.forEach {
                val heading = it.heading
                listCvs.forEach {
                    if (heading == it[0]){
                        drawableName = it[1]
                        Log.d("DrawableName", it[1])
                    }

                    if(drawableName == ""){
                        drawableName = "wifi"
                    }

            }
                drawableNameList.add(drawableName)

            }


        }


        if(listEquipements.size > 2){
            val newList = mutableListOf<Equipement>(listEquipements[0],listEquipements[1])
            equipement_recyclerview.adapter =
                EquipementAdapter(newList, drawableNameList, this@DetailsHotel)
                button_description.visibility  = View.VISIBLE
        }else{
            equipement_recyclerview.adapter =
                EquipementAdapter(listEquipements, drawableNameList,this@DetailsHotel)
            button_description.visibility  = View.GONE
        }


        button_proche.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Lire plus")
                setMessage("${listProche}")
                setPositiveButton(android.R.string.ok) { _, _ ->
                }
                show()
            }
        }

        button_description.setOnClickListener {
            val createdialog = androidx.appcompat.app.AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.equipements_popup, null)
            val recyclerview = view.findViewById<RecyclerView>(R.id.equipement_recyclerview)
            val okbutton = view.findViewById<Button>(R.id.bt_ok)
            createdialog.setView(view)
            createdialog.setTitle("Voir plus")
            recyclerview.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerview.adapter =
                EquipementAdapter(listEquipements,drawableNameList,this)
            val alert = createdialog.create()
            alert.show()
            okbutton.setOnClickListener {
                alert.dismiss()
            }

        }


        offers_recyclerview.adapter = RoomAdapter(listRooms)

     if (hotel?.image_url==null){
        detail_hotel_imageview.setImageResource(R.drawable.hotel)

            }else{
            Glide.with(this@DetailsHotel)
                .load(hotel?.image_url)
                .centerCrop()
                .into(detail_hotel_imageview)}




        when (hotel?.rate) {
            1 -> one_star_layout.visibility = View.VISIBLE
            2 -> two_star_layout.visibility = View.VISIBLE
            3 -> three_star_layout.visibility = View.VISIBLE
            4 -> four_star_layout.visibility = View.VISIBLE
            5 -> five_star_layout.visibility = View.VISIBLE
            else -> {
            }
        }

        var adresse = mutableListOf<String>()
        hotel?.adresse!!.forEach {
            if(it == "null"){
                adresse.add("NC")
            }else{
                adresse.add(it)
            }
        }

        var nom = "${hotel?.hotelName}".toLowerCase()
        detail_hotel_nom_textview.text = formatString(nom)
        detail_hotel_adresse_texview.setTypeface(null, Typeface.ITALIC)
        detail_hotel_adresse_texview.text =
            "${adresse.get(0)} ${adresse.get(1)}, ${adresse.get(2)}, ${adresse.get(
                3)}"









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

                destination = adresse[2]
                Log.d("zzz", " somme =$destination")


                createdialog.setView(view)
                createdialog.setTitle("Créer")
                val alert = createdialog.show()
                editText.setOnClickListener {
                    rangeDatePickerPrimeCalendar(editText)
                }


                okbutton.setOnClickListener {


                    if (!editTitre.text.isEmpty() && !editDate.text.isEmpty()) {

                        val database =
                            Room.databaseBuilder(this, AppDatabase::class.java, "savedDatabase")
                                .build()

                        citydao = database.getCityDao()
                        runBlocking {
                            val citie = citydao.getCity(destination)
                            image = citie.cover_image_url.toString()

                        }

                        var jourfin = LocalDate.parse(date_fin, DateTimeFormatter.ISO_DATE)
                        var jourdebut = LocalDate.parse(date_debut, DateTimeFormatter.ISO_DATE)
                        var jour = jourfin.compareTo(jourdebut) + 1
                        Log.d("zzz", "jour =$jour ")
                        var somme = view.et_nb_voyageur.selectedItem.toString().toInt() * 100 *jour
                        Log.d("zzz", " somme =$somme")

                        budget = somme.toString()

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

    private fun initializeAdultsList(result : String?){
        var substring = result?.substringBefore("]")
        substring = substring?.substringAfter("[")
        listAdults?.clear()
        if(substring?.length == 1){
            listAdults.add(substring)
        }else {
            listAdults = substring?.split(", ") as ArrayList<String>
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
    }




