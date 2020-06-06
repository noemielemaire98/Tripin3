package com.example.tripin.find.voyage

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.resources.FlightOfferSearch
import com.amadeus.resources.HotelOffer
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.example.tripin.R
import com.example.tripin.data.*
import com.example.tripin.find.activity.ActivityAdapterGlobal
import com.example.tripin.find.activity.ActivitybyCity
import com.example.tripin.find.flight.FlightsAdapter
import com.example.tripin.find.flight.IgnoreAccentsArrayAdapter
import com.example.tripin.find.hotel.HotelsAdapter
import com.example.tripin.model.Activity
import com.example.tripin.model.Flight
import com.example.tripin.model.Hotel
import com.example.tripin.model.Voyage
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.createvoyage_popup.view.*
import kotlinx.android.synthetic.main.fragment_find_voyage.*
import kotlinx.android.synthetic.main.fragment_find_voyage.view.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import android.app.Activity as AppActivity

/**
 * A simple [Fragment] subclass.
 */
class FindVoyage : Fragment() {

    private lateinit var dateDepart: String
    private lateinit var dateRetour: String
    private lateinit var lieuDepartIata: String
    private lateinit var lieuRetourIata: String
    private lateinit var lieuRetourName: String
    private lateinit var travelClass: String

    private var listFavoris = arrayListOf<Boolean>()
    private var listFavorisHotels = arrayListOf<Boolean>()
    private var listHotels: MutableList<Hotel>? = mutableListOf()
    private val listEquipementFormatted = mutableListOf<List<String>>()
    private var flightsList: MutableList<MutableList<Flight>> = mutableListOf()
    private var flightsListSimple: MutableList<Flight>? = mutableListOf()
    private var activitiesList: List<Activity>? = emptyList()

    private var voyageDaoSavedSave: VoyageDao? = null
    private var listVoyagesBdd: MutableList<Voyage>? = null
    private var flightDao: FlightDao? = null
    private var cityDao: CityDao? = null
    private var activityDaoSaved: ActivityDao? = null
    private var activityDaoSearch: ActivityDao? = null
    private var hotelDaoSearch: HotelDao? = null
    private var hotelDaoSaved: HotelDao? = null
    private var voyageDaoSaved: VoyageDao? = null

    private lateinit var globalRecyclerView: RecyclerView
    private var flightsAdapter: FlightsAdapter? = null
    private var hotelsAdapter: HotelsAdapter? = null
    private var activityAdapter: ActivityAdapterGlobal? = null
    private var mergeAdapter = MergeAdapter()

    private var flightsDone = false
    private var hotelsDone = false
    private var activitiesDone = false
    private var activityCreate = true
    private val lang: String = "fr-FR"
    private val monnaie: String = "EUR"
    private var cityId = ""
    private var categories = ""
    private var query = ""
    private var priceRange = "0,100"
    private var priceMaxactivites = "100"
    private var priceMaxhotels = "200"
    private var image = ""
    private var destination = ""
    private var budget = ""
    private var voyageFavoris = false

    private var animatedHide = false
    private var animatedShow = false

    private var mBundleRecyclerViewState: Bundle? = null
    private var mListState: Parcelable? = null

    private val amadeus: Amadeus = Amadeus
        .builder("TGvUHAv2qE6aoqa2Gg44ZZGpvDIEGwYs", "a16JGxtWdWBPtTGB")
        .build()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_find_voyage, container, false)

        globalRecyclerView = view.findViewById(R.id.global_recyclerview)
        globalRecyclerView.layoutManager = LinearLayoutManager(activity)

        val hideInput = view.findViewById<Button>(R.id.hide_input)
        val simpleButton = view.findViewById<Button>(R.id.simple_button)
        val allerDate = view.findViewById<EditText>(R.id.aller_date)
        val returnDate = view.findViewById<EditText>(R.id.return_date)
        val btnSearch = view.findViewById<Button>(R.id.btn_search)
        val autoTextViewDepart = view.findViewById<AutoCompleteTextView>(R.id.autoTextViewDepart)
        val autoTextViewRetour = view.findViewById<AutoCompleteTextView>(R.id.autoTextViewRetour)
        val allertypeRadiogroup = view.findViewById<RadioGroup>(R.id.allerType_radiogroup)
        val passengersNumberTextView =
            view.findViewById<AutoCompleteTextView>(R.id.passengers_number)
        val activitiesNumberTextView =
            view.findViewById<AutoCompleteTextView>(R.id.activities_number)
        val hotelsNumberTextView = view.findViewById<AutoCompleteTextView>(R.id.hotels_number)
        val scrollToTopArrow =
            view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.scroll_to_top_arrow)
        val voyageTopLevelScrollView = view.findViewById<ScrollView>(R.id.voyageTopLevel_ScrollView)
        val voyageTopLevelLayout = view.findViewById<RelativeLayout>(R.id.voyageTopLevel_Layout)
        val travelClassEdit = view.findViewById<AutoCompleteTextView>(R.id.travelClassEdit)
        val btnMuseum = view.findViewById<Button>(R.id.cat_museum)
        val btnSport = view.findViewById<Button>(R.id.cat_sport)
        val btnFood = view.findViewById<Button>(R.id.cat_food)
        val btnFun = view.findViewById<Button>(R.id.cat_fun)
        val btnNight = view.findViewById<Button>(R.id.cat_night)
        val btnOther = view.findViewById<Button>(R.id.cat_other)
        val btPrice = view.findViewById<ImageButton>(R.id.bt_price_filter)
        val fabAddVoyage = view.findViewById<ImageView>(R.id.fab_addVoyage)
        val fabFavVoyage = view.findViewById<ImageView>(R.id.fab_favVoyage)


        // Initialise la BDD
        val databaseVoyage =
            Room.databaseBuilder(requireContext(), AppDatabase::class.java, "voyageSearchDatabase")
                .build()
        val databaseSaved =
            Room.databaseBuilder(requireContext(), AppDatabase::class.java, "savedDatabase")
                .build()

        val databaseSavedVoyage =
            Room.databaseBuilder(requireContext(), AppDatabase::class.java, "savedVoyageDatabase")
                .build()

        voyageDaoSavedSave = databaseSavedVoyage.getVoyageDao()



        flightDao = databaseVoyage.getFlightDao()
        cityDao = databaseSaved.getCityDao()
        activityDaoSearch = databaseVoyage.getActivityDao()
        activityDaoSaved = databaseSaved.getActivityDao()
        hotelDaoSearch = databaseVoyage.getHotelDao()
        hotelDaoSaved = databaseSaved.getHotelDao()
        voyageDaoSaved = databaseSaved.getVoyageDao()

        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        // listener sur les boutons activités = change de couleur
        listenerBouton(btnMuseum, requireContext())
        listenerBouton(btnSport, requireContext())
        listenerBouton(btnFun, requireContext())
        listenerBouton(btnNight, requireContext())
        listenerBouton(btnFood, requireContext())
        listenerBouton(btnOther, requireContext())

        // listener sur le prix
        btPrice.setOnClickListener {

            val dialog = AlertDialog.Builder(activity)
            val dialogView = layoutInflater.inflate(
                R.layout.layout_dialog_price_voyage,
                view.findViewById(android.R.id.content)
            )

            val seekbarActivites = dialogView.findViewById<BubbleSeekBar>(R.id.seekbarActivités)
            val seekbarHotels = dialogView.findViewById<BubbleSeekBar>(R.id.seekbarHotels)
            seekbarActivites.setProgress(priceMaxactivites.toFloat())
            seekbarHotels.setProgress(priceMaxhotels.toFloat())
            dialog.setView(dialogView)
            dialog.setCancelable(false)
            dialog.setPositiveButton(android.R.string.ok) { _, _ ->
                priceRange = "0,${seekbarActivites.progress}"
                priceMaxactivites = seekbarActivites.progress.toString()
                priceMaxhotels = seekbarHotels.progress.toString()
            }
            dialog.show()
        }

        scrollToTopArrow.setOnClickListener { voyageTopLevelScrollView.smoothScrollTo(0, 0) }

        // Hide the arrow at the beginning when the screen starts
        scrollToTopArrow.visibility = View.GONE

        scope.launch {

            listVoyagesBdd = voyageDaoSavedSave?.getVoyage()?.toMutableList()

            // Positions for the arrow when is hidden and visible
            val whenVisibleMargin = convertDpToPixel(15f, requireContext())
            val whenHideMargin = convertDpToPixel(-85f, requireContext())

            voyageTopLevelScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY >= 600) {
                    if (!animatedShow) {
                        scrollToTopArrow.visibility = View.VISIBLE
                        val params = scrollToTopArrow.layoutParams as RelativeLayout.LayoutParams
                        val animator =
                            ValueAnimator.ofInt(params.rightMargin, whenVisibleMargin.toInt())
                        animator.addUpdateListener { valueAnimator ->
                            params.rightMargin = valueAnimator.animatedValue as Int
                            scrollToTopArrow.requestLayout()
                        }
                        animator.duration = 300
                        animator.start()
                        animatedShow = true
                        animatedHide = false
                    }
                } else {
                    if (!animatedHide) {
                        scrollToTopArrow.visibility = View.VISIBLE
                        val params = scrollToTopArrow.layoutParams as RelativeLayout.LayoutParams
                        val animator =
                            ValueAnimator.ofInt(params.rightMargin, whenHideMargin.toInt())
                        animator.addUpdateListener { valueAnimator ->
                            params.rightMargin = valueAnimator.animatedValue as Int
                            scrollToTopArrow.requestLayout()
                        }
                        animator.duration = 300
                        animator.start()
                        animatedHide = true
                        animatedShow = false
                    }
                }

            }

            val listCities = cityDao?.getCity()

            val listAirportsFormatted = mutableListOf<String>()

            // Affiche uniquement les infos utiles des villes dans une liste
            listCities?.map { itMap ->
                listAirportsFormatted.add(
                    itMap.name.toString() + " (" + itMap.iataCode
                        .toUpperCase(
                            Locale.ROOT
                        ) + ")"
                )
            }
            // Initialise la liste déroulante du lieu de départ
            val adapterLieuDepart = IgnoreAccentsArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, listAirportsFormatted.toTypedArray()
            )
            // Initialise la liste déroulante du lieu d'arrivée
            val adapterLieuArrival = IgnoreAccentsArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, listAirportsFormatted.toTypedArray()
            )
            //empty autoTextView if not chosen from list
            clearFocusAutoTextView(autoTextViewDepart)
            clearFocusAutoTextView(autoTextViewRetour)

            // Initialise la liste déroulante du nombre de passagers
            val passengersNumber = resources.getStringArray(R.array.passengersNumber)
            val adapterPassengers = IgnoreAccentsArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, passengersNumber
            )

            // Initialise la liste déroulante des classes de vol
            val travelClassList = resources.getStringArray(R.array.travelClass)
            val adapterTravelClass = IgnoreAccentsArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, travelClassList
            )

            withContext(Dispatchers.Main) {

                autoTextViewDepart.setAdapter(adapterLieuDepart)
                autoTextViewRetour.setAdapter(adapterLieuArrival)
                passengersNumberTextView.setAdapter(adapterPassengers)
                travelClassEdit.setAdapter(adapterTravelClass)
                activitiesNumberTextView.setAdapter(adapterPassengers)
                hotelsNumberTextView.setAdapter(adapterPassengers)

                // Affiche le calendrier pour choisir la date d'aller
                allerDate.setOnClickListener {
                    hideKeyboard()
                    voyageTopLevelLayout.requestFocus()
                    if (return_dateLayout.visibility == View.VISIBLE) { // Si c'est un voyage aller-retour

                        rangeDatePickerPrimeCalendar()

                    } else if (return_dateLayout.visibility == View.GONE) { // Si c'est un voyage avec aller simple

                        val singleDayPickCallback = SingleDayPickCallback { date ->
                            val parser =
                                SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                            val formatterDate =
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val parsedDate =
                                formatterDate.format(parser.parse(date.shortDateString)!!)
                            allerDate.setText(parsedDate)
                        }

                        val today = CivilCalendar()

                        if (allerDate.text.toString() != "") { // Si une date a déjà été choisie
                            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val calStart = CivilCalendar()
                            calStart.timeInMillis = df.parse(allerDate.text.toString())!!.time

                            val datePickerT = PrimeDatePicker.dialogWith(today)
                                .pickSingleDay(singleDayPickCallback)
                                .initiallyPickedSingleDay(calStart)
                                .firstDayOfWeek(Calendar.MONDAY)
                                .minPossibleDate(today)
                                .build()

                            datePickerT.show(childFragmentManager, "PrimeDatePickerBottomSheet")
                        } else { // Si aucune date n'a encore été choisie

                            val datePickerT = PrimeDatePicker.dialogWith(today)
                                .pickSingleDay(singleDayPickCallback)
                                .firstDayOfWeek(Calendar.MONDAY)
                                .minPossibleDate(today)
                                .build()

                            datePickerT.show(childFragmentManager, "PrimeDatePickerBottomSheet")
                        }
                    }
                }

                // Affiche le calendrier pour choisir la date de retour
                returnDate.setOnClickListener {
                    hideKeyboard()
                    voyageTopLevelLayout.requestFocus()
                    rangeDatePickerPrimeCalendar()
                }

                // Montre toute la liste déroulante à chaque fois
                passengersNumberTextView.setOnClickListener {
                    hideKeyboard()
                    voyageTopLevelLayout.requestFocus()
                    passengersNumberTextView.showDropDown()
                }

                // Montre toute la liste déroulante à chaque fois
                travelClassEdit.setOnClickListener {
                    hideKeyboard()
                    voyageTopLevelLayout.requestFocus()
                    travelClassEdit.showDropDown()
                }

                activitiesNumberTextView.setOnClickListener {
                    hideKeyboard()
                    voyageTopLevelLayout.requestFocus()
                }

                hotelsNumberTextView.setOnClickListener {
                    hideKeyboard()
                    voyageTopLevelLayout.requestFocus()
                }

                hideInput.setOnClickListener {
                    if (layout_search.visibility == View.VISIBLE) {
                        layout_search.visibility = View.GONE
                    } else {
                        layout_search.visibility = View.VISIBLE
                    }
                }

                // Bouton pour lancer la recherche
                btnSearch.setOnClickListener {
                    hideKeyboard()
                    voyageTopLevelLayout.requestFocus()

                    if (autoTextViewDepart.text.toString() == "" || autoTextViewRetour.text.toString() == "") { // Si les lieux ne sont pas bien spécifiés
                        Toast.makeText(
                            requireContext(),
                            "Veuillez choisir les destinations dans les listes déroulantes",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    flightsDone = false
                    hotelsDone = false
                    activitiesDone = false

                    // Récupère les code IATA des lieux
                    val strDepart = autoTextViewDepart.text.toString()
                    val keptDepartIata = strDepart.substringAfterLast("(")
                    lieuDepartIata = keptDepartIata.substringBeforeLast(")")
                    val strRetour = autoTextViewRetour.text.toString()

                    lieuRetourName = strRetour.substringBefore(" (")
                    val keptRetourIata = strRetour.substringAfterLast("(")
                    lieuRetourIata = keptRetourIata.substringBeforeLast(")")

                    if (allerDate.text.toString() != "" && returnDate.text.toString() != "") { // Si lieux bien spécifiés et voyage aller-retour

                        dateRetour = returnDate.text.toString()
                        beginSearchExported(dateRetour)

                    } else if (allerDate.text.toString() != "" && return_dateLayout.visibility == View.GONE) { // Si voyage aller-simple

                        dateRetour = ""
                        beginSearchExported(dateRetour)

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Veuillez remplir tous les champs",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                // Choix du type de voyage (aller simple ou aller-retour)
                allertypeRadiogroup.setOnCheckedChangeListener { _, checkedId ->
                    val radio: RadioButton = view.findViewById(checkedId)
                    if (radio.id == simpleButton.id) {
                        return_dateLayout.visibility = View.GONE
                        returnDate.setText("")

                    } else {
                        return_dateLayout.visibility = View.VISIBLE
                    }
                }

                //AU CLIC SUR LE BOUTON AJOUT A UN VOYAGE
                fabAddVoyage.setOnClickListener {
                    val listCheckeditems = ArrayList<Boolean>()
                    val listVoyage: ArrayList<String> = arrayListOf()
                    runBlocking {
                        val voyage = voyageDaoSaved?.getVoyage()
                        flightsListSimple = flightDao?.getFlights()?.toMutableList()
                        activitiesList = activityDaoSearch?.getActivity()
                        listHotels = hotelDaoSearch?.getHotels()?.toMutableList()
                        voyage?.map {

                            listVoyage.add(it.titre)
                            var equalsNumber = 0
                            it.list_flights?.map {
                                flightsListSimple?.map { itF ->
                                    if (it.uuid == itF.uuid && it.lieuDepart == itF.lieuDepart) {
                                        equalsNumber += 1
                                    }
                                }
                            }

                            it.list_hotels?.map {
                                listHotels?.map { itH ->
                                    if (it.hotelId == itH.hotelId) {
                                        equalsNumber += 1
                                    }
                                }
                            }
                            it.list_activity?.map {
                                activitiesList?.map { itA ->
                                    if (it.title == itA.title) {
                                        equalsNumber += 1
                                    }
                                }
                            }
                            val sizeTotal =
                                flightsListSimple?.size!! + listHotels?.size!! + activitiesList?.size!!

                            if (sizeTotal == equalsNumber) {
                                listCheckeditems.add(true)
                            } else {
                                listCheckeditems.add(false)
                            }
                        }
                    }
                    val plusdialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    plusdialog.setTitle("Dossier de voyage")
                    val listChoix = arrayListOf<String>()

                    if (listVoyage.isNullOrEmpty()) {
                        plusdialog.setMessage("Vous n'avez constitué aucun dossier de voyage, cliquez sur créer")
                    } else {
                        hasVoyage(
                            listVoyage,
                            listCheckeditems,
                            plusdialog,
                            listChoix,
                            flightsListSimple!!,
                            activitiesList!!.toMutableList(),
                            listHotels!!
                        )
                    }

                    plusdialog.setPositiveButton(android.R.string.ok) { _, _ ->
                        plusdialog.show().dismiss()
                    }


                    plusdialog.setNeutralButton("Créer") { _, _ ->


                        val createDialog = AlertDialog.Builder(context)

                        val viewPop = LayoutInflater.from(context).inflate(
                            R.layout.createvoyage_popup_findvoyage,
                            view.findViewById(android.R.id.content),
                            false
                        )

                        val okButton = viewPop.findViewById<Button>(R.id.bt_ok)
                        val returnButton = viewPop.findViewById<Button>(R.id.bt_retour)
                        val editTitre = viewPop.findViewById<EditText>(R.id.et_titre)
                        createDialog.setView(viewPop)
                        createDialog.setTitle("Créer")
                        val alert = createDialog.show()

                        okButton.setOnClickListener {
                            var exist = false
                            listVoyage.map { itL ->
                                if (editTitre.text.toString() == itL) {
                                    exist = true
                                }
                            }
                            when {
                                exist -> {
                                    Toast.makeText(
                                        context,
                                        "Ce nom de voyage existe déjà, veuillez en chosir un autre",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    editTitre.setText("")
                                }
                                editTitre.text.isNotEmpty() -> {

                                    var date = ""
                                    val poto =
                                        (flightsListSimple?.get(0)?.prixTotal!! / flightsListSimple?.get(
                                            0
                                        )?.prixParPassager!!).roundToInt()
                                    run loop@{
                                        flightsListSimple?.map {
                                            if (it.retour == 1) {
                                                date = it.dateDepart
                                                return@loop
                                            } else {
                                                date = ""
                                            }
                                        }
                                    }
                                    val voyage = Voyage(
                                        0,
                                        viewPop.et_titre.text.toString(),
                                        flightsListSimple?.get(0)?.dateDepart,
                                        date,
                                        image,
                                        poto,
                                        emptyList(),
                                        emptyList(),
                                        emptyList(),
                                        destination,
                                        budget
                                    )
                                    runBlocking {
                                        voyageDaoSaved?.addVoyage(voyage)
                                    }
                                    listVoyage.add(editTitre.text.toString())
                                    plusdialog.show().dismiss()
                                    fabAddVoyage.performClick()
                                    alert.dismiss()
                                }
                                else -> {
                                    Toast.makeText(
                                        context,
                                        "Veuillez saisir tous les champs",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        returnButton.setOnClickListener {
                            alert.dismiss()
                            plusdialog.show().dismiss()
                            fabAddVoyage.performClick()
                        }
                    }
                    plusdialog.show()
                }


                val listVoyageSaved: ArrayList<String> = arrayListOf()

                flightsListSimple = flightDao?.getFlights()?.toMutableList()
                activitiesList = activityDaoSearch?.getActivity()
                listHotels = hotelDaoSearch?.getHotels()?.toMutableList()
                listVoyagesBdd?.map {

                    listVoyageSaved.add(it.titre)
                    var equalsNumber = 0
                    it.list_flights?.map {
                        flightsListSimple?.map { itF ->
                            if (it.uuid == itF.uuid && it.lieuDepart == itF.lieuDepart) {
                                equalsNumber += 1
                            }
                        }
                    }

                    it.list_hotels?.map {
                        listHotels?.map { itH ->
                            if (it.hotelId == itH.hotelId) {
                                equalsNumber += 1
                            }
                        }
                    }
                    it.list_activity?.map {
                        activitiesList?.map { itA ->
                            if (it.title == itA.title) {
                                equalsNumber += 1
                            }
                        }
                    }
                    val sizeTotal =
                        flightsListSimple?.size!! + listHotels?.size!! + activitiesList?.size!!

                    voyageFavoris = if (sizeTotal == equalsNumber) {
                        fab_favVoyage.setImageResource(R.drawable.ic_favorite_black_24dp)
                        true
                    } else {
                        fab_favVoyage.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                        false
                    }
                }

                fabFavVoyage.setOnClickListener {

                    if (!voyageFavoris) {

                        val createDialog = AlertDialog.Builder(context)

                        val viewPop = LayoutInflater.from(context).inflate(
                            R.layout.createvoyage_popup_findvoyage,
                            view.findViewById(android.R.id.content),
                            false
                        )

                        val okButton = viewPop.findViewById<Button>(R.id.bt_ok)
                        val returnButton = viewPop.findViewById<Button>(R.id.bt_retour)
                        val editTitre = viewPop.findViewById<EditText>(R.id.et_titre)
                        createDialog.setView(viewPop)
                        createDialog.setTitle("Créer")
                        val alert = createDialog.show()

                        okButton.setOnClickListener {
                            var exist = false
                            listVoyageSaved.map { itL ->
                                if (editTitre.text.toString() == itL) {
                                    exist = true
                                }
                            }
                            when {
                                exist -> {
                                    Toast.makeText(
                                        context,
                                        "Ce nom de voyage existe déjà, veuillez en chosir un autre",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    editTitre.setText("")
                                }
                                editTitre.text.isNotEmpty() -> {
                                    var date = ""
                                    val poto =
                                        (flightsListSimple?.get(0)?.prixTotal!! / flightsListSimple?.get(
                                            0
                                        )?.prixParPassager!!).roundToInt()
                                    run loop@{
                                        flightsListSimple?.map {
                                            if (it.retour == 1) {
                                                date = it.dateDepart
                                                return@loop
                                            } else {
                                                date = ""
                                            }
                                        }
                                    }

                                    val voyage = Voyage(
                                        0,
                                        viewPop.et_titre.text.toString(),
                                        flightsListSimple?.get(0)?.dateDepart,
                                        date,
                                        image,
                                        poto,
                                        activitiesList,
                                        flightsListSimple,
                                        listHotels,
                                        destination,
                                        budget
                                    )

                                    runBlocking {
                                        voyageDaoSavedSave?.addVoyage(voyage)
                                    }

                                    voyageFavoris = true
                                    fab_favVoyage.setImageResource(R.drawable.ic_favorite_black_24dp)
                                    Toast.makeText(
                                        context,
                                        "Le voyage a bien été ajouté aux favoris",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    listVoyageSaved.add(editTitre.text.toString())
                                    alert.dismiss()
                                }
                                else -> {
                                    Toast.makeText(
                                        context,
                                        "Veuillez saisir tous les champs",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        returnButton.setOnClickListener {
                            alert.dismiss()
                        }


                    } else if (voyageFavoris) {

                        runBlocking {
                            voyageDaoSavedSave?.deleteFindVoyage("viewPop.et_titre.text.toString()")
                        }


                        voyageFavoris = false

                        fab_favVoyage.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                        Toast.makeText(
                            context,
                            "Le voyage a bien été supprimé des favoris",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    runBlocking {
                        listVoyagesBdd = voyageDaoSavedSave?.getVoyage()?.toMutableList()
                    }

                    Log.d("totototo", "onCreateView: $listVoyagesBdd")
                }

            }

        }

        return view
    }

    private fun hasVoyage(
        list_voyage: ArrayList<String>,
        list_checkedItems: ArrayList<Boolean>,
        plusdialog: androidx.appcompat.app.AlertDialog.Builder,
        list_choix: ArrayList<String>,
        flights: MutableList<Flight>,
        activities: MutableList<Activity>,
        hotels: MutableList<Hotel>
    ) {
        plusdialog.setMultiChoiceItems(
            list_voyage.toTypedArray(),
            list_checkedItems.toBooleanArray()
        ) { _, which: Int, isChecked ->
            // Update the current focused item's checked status
            list_checkedItems[which] = isChecked
            if (isChecked) {
                list_choix.add(list_voyage[which])
                runBlocking {
                    val voyage = voyageDaoSaved?.getVoyageByTitre(list_voyage[which])


                    val ancienneListFlights = voyage!!.list_flights?.toMutableList()
                    val ancienneListActivites = voyage.list_activity?.toMutableList()
                    val ancienneListHotels = voyage.list_hotels?.toMutableList()
                    ancienneListFlights?.addAll(flights)
                    ancienneListActivites?.addAll(activities)
                    ancienneListHotels?.addAll(hotels)
                    val nouvelleListeFlights = ancienneListFlights?.toList()
                    val nouvelleListeActivites = ancienneListActivites?.toList()
                    val nouvelleListeHotels = ancienneListHotels?.toList()
                    voyage.list_flights = nouvelleListeFlights
                    voyage.list_activity = nouvelleListeActivites
                    voyage.list_hotels = nouvelleListeHotels



                    voyageDaoSaved?.updateVoyage(voyage)
                }
                Toast.makeText(
                    requireContext(),
                    "Le voyage a bien été ajouté à ${list_voyage[which]}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                list_choix.remove(list_voyage[which])
                runBlocking {
                    val voyage = voyageDaoSaved?.getVoyageByTitre(list_voyage[which])
                    val ancienneListFlights = voyage!!.list_flights?.toMutableList()
                    val ancienneListActivites = voyage.list_activity?.toMutableList()
                    val ancienneListHotels = voyage.list_hotels?.toMutableList()
                    ancienneListFlights?.removeAll(flights)
                    ancienneListActivites?.removeAll(activities)
                    ancienneListHotels?.removeAll(hotels)
                    val nouvelleListeFlights = ancienneListFlights?.toList()
                    val nouvelleListeActivites = ancienneListActivites?.toList()
                    val nouvelleListeHotels = ancienneListHotels?.toList()
                    voyage.list_flights = nouvelleListeFlights
                    voyage.list_activity = nouvelleListeActivites
                    voyage.list_hotels = nouvelleListeHotels
                    voyageDaoSaved?.updateVoyage(voyage)
                }
                Toast.makeText(
                    requireContext(),
                    "Le voyage a bien été supprimé de ${list_voyage[which]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    override fun onResume() {
        super.onResume()

        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        mergeAdapter = MergeAdapter()

        scope.launch {
            val flights = flightDao?.getFlights()
            val activities = activityDaoSearch?.getActivity()
            val hotels = hotelDaoSearch?.getHotels()
            if (!flights.isNullOrEmpty()) { // S'il y a des vols de trouvés
                layoutNoFlightAvailable.visibility = View.GONE
                val flightsList: MutableList<MutableList<Flight>> = mutableListOf()
                var testlist: MutableList<Flight> = mutableListOf()
                var travelId = 1
                var position = 0
                // map pour regrouper les vols d'un même itinéraire
                flights.map {
                    val flight = flights[it.id - 1]
                    if (it.travelId != travelId) { // Si appartient au même itinéraire
                        flightsList.add(testlist)
                        testlist = mutableListOf()
                        travelId = it.travelId
                    }
                    testlist.add(flight)
                    position += 1
                    if (flights.size == it.id) {
                        flightsList.add(testlist)
                    }
                }
                withContext(Dispatchers.Main) {
                    flightsAdapter = FlightsAdapter(flightsList, requireView())
                    mergeAdapter.addAdapter(flightsAdapter!!)
                }
            }
            if (!hotels.isNullOrEmpty()) {
                val listHotelsBdd = hotelDaoSaved?.getHotels()

                hotels.map {
                    val hotelId = it.hotelId
                    var matchBdd = false
                    listHotelsBdd?.forEach { itList ->
                        if (itList.hotelId == hotelId) {
                            listFavorisHotels.add(true)
                            matchBdd = true
                        }
                    }
                    if (!matchBdd) {
                        listFavorisHotels.add(false)
                    }
                }
                withContext(Dispatchers.Main) {
                    hotelsAdapter = HotelsAdapter(hotels, listFavorisHotels, mutableListOf(),"","")
                    mergeAdapter.addAdapter(hotelsAdapter!!)
                }
            }
            if (!activities.isNullOrEmpty()) {
                val listActivitiesBdd = activityDaoSaved?.getActivity()

                activities.map {
                    val titre = it.title
                    var matchBdd = false
                    listActivitiesBdd?.forEach { itList ->
                        if (itList.title == titre) {
                            listFavoris.add(true)
                            matchBdd = true
                        }
                    }
                    if (!matchBdd) {
                        listFavoris.add(false)
                    }
                }
                withContext(Dispatchers.Main) {
                    activityAdapter = ActivityAdapterGlobal(activities.toMutableList(), listFavoris)
                    mergeAdapter.addAdapter(activityAdapter!!)
                }
            }
            // S'il n'y a pas de vols et qu'une recherche a été effectuée, affiche l'image aucun vol dispo
            if (!activityCreate && flights.isNullOrEmpty() && hotels.isNullOrEmpty() && activities.isNullOrEmpty()) {
                withContext(Dispatchers.Main) {
                    layoutNoFlightAvailable.visibility = View.VISIBLE
                }
            }
            if (!flights.isNullOrEmpty() || !hotels.isNullOrEmpty() || !activities.isNullOrEmpty()) {
                withContext(Dispatchers.Main) {
                    loadingPanel.visibility = View.GONE // cache roue de chargement
                    layoutRecyclerViewVoyage.visibility = View.VISIBLE
                    cardViewVoyage.visibility = View.VISIBLE
                    globalRecyclerView.adapter = mergeAdapter

                    if (mBundleRecyclerViewState != null) {
                        mListState = mBundleRecyclerViewState!!.getParcelable("keyR")
                        globalRecyclerView.layoutManager?.onRestoreInstanceState(mListState)
                    }
                }
            }
        }
        //     }
    }

    override fun onPause() {
        super.onPause()

        mBundleRecyclerViewState = Bundle()

        mListState = globalRecyclerView.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState!!.putParcelable("keyR", mListState)
    }

    // Lancement de la recherche

    private fun beginSearchExported(dateRetour: String) {
        dateDepart = aller_date.text.toString()
        val nbActivities = activities_number.text.toString()
        val nbHotels = hotels_number.text.toString().toInt()

        mergeAdapter = MergeAdapter()
        travelClass =
            if (travelClassEdit.text.toString() != "PREMIUM ECO") travelClassEdit.text.toString() else "PREMIUM_ECONOMY"
        val nbAdults = passengers_number.text.toString().toInt()
        //     layout_search.visibility = View.GONE // cache le formulaire
        layoutRecyclerViewVoyage.visibility = View.GONE
        cardViewVoyage.visibility = View.GONE
        loadingPanel.visibility = View.VISIBLE // affiche la roue de chargement
        layoutNoFlightAvailable.visibility = View.GONE // cache l'image aucun vol dispo

        runBlocking {
            flightDao?.deleteFlights()
            hotelDaoSearch?.deleteHotels()
            activityDaoSearch?.deleteActivity()
        }
        runBlocking {
            searchFlights(
                dateDepart,
                dateRetour,
                lieuDepartIata,
                lieuRetourIata,
                travelClass,
                nbAdults
            )
            searchActivities(lieuRetourName, nbActivities, dateDepart, dateRetour, priceRange)
            Handler().postDelayed({
                searchHotels(lieuRetourIata, nbHotels, dateDepart, dateRetour, "0", priceMaxhotels)
            }, 100)
        }
        activityCreate = false
        globalRecyclerView.adapter = mergeAdapter
    }

    private fun searchCompleted(view: View) {
        if (flightsDone && hotelsDone && activitiesDone) {
            view.loadingPanel.visibility = View.GONE // cache roue de chargement
            layoutRecyclerViewVoyage.visibility = View.VISIBLE
            cardViewVoyage.visibility = View.VISIBLE
            if (flightsList.isNullOrEmpty() && listHotels.isNullOrEmpty() && activitiesList.isNullOrEmpty()) {
                view.layoutNoFlightAvailable.visibility = View.VISIBLE
            }
        }
    }

    // Appel API et enregistrement BDD
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun searchFlights(
        dateDepart: String,
        dateRetour: String,
        lieuDepart: String,
        lieuRetour: String,
        travelClass: String,
        nbAdults: Int
    ) {
        flightsList.clear()
        flightsListSimple?.clear()
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        flightDao?.deleteFlights()

        scope.launch {

            var flightOffersSearches: Array<FlightOfferSearch> = emptyArray()
            if (dateRetour != "") { // si aller-retour
                try {
                    flightOffersSearches = amadeus.shopping.flightOffersSearch[Params
                        .with("originLocationCode", lieuDepart)
                        .and("destinationLocationCode", lieuRetour)
                        .and("departureDate", dateDepart)
                        .and("returnDate", dateRetour)
                        .and("adults", nbAdults)
                        .and("max", 1)
                        .and("travelClass", travelClass)]
                } catch (e: Exception) {
                    Log.d("ErrorFlight", e.toString())
                }
            } else { // si aller simple
                try {
                    flightOffersSearches = amadeus.shopping.flightOffersSearch[Params
                        .with("originLocationCode", lieuDepart)
                        .and("destinationLocationCode", lieuRetour)
                        .and("departureDate", dateDepart)
                        .and("adults", nbAdults)
                        .and("max", 1)
                        .and("travelClass", travelClass)]
                } catch (e: Exception) {
                    Log.d("ErrorFlight", e.toString())
                }
            }

            withContext(Dispatchers.Main) {

                // recupère les noms des compagnies aériennes
                val airlinesNamesCsv =
                    requireContext().resources.openRawResource(R.raw.optd_airline)
                val rows: List<Map<String, String>> =
                    csvReader().readAllWithHeader(airlinesNamesCsv)

                var id = 1 // pour incrémenter les id
                var carrierName = ""
                var carrierCodeLogo = ""

                flightOffersSearches.map { itFlight ->
                    var retour = 0 // pour savoir si voyage aller ou voyage retour
                    var uuidSomme = ""
                    val listFlightAdd = mutableListOf<Flight>()
                    itFlight.itineraries.map { itItineraries ->
                        val nbEscales = itItineraries.segments.size - 1
                        itItineraries.segments.map { itSegments ->

                            val sec = Duration.parse(itItineraries.duration).seconds
                            val dureeFormat = String.format(
                                "%02dh%02d", TimeUnit.SECONDS.toHours(sec),
                                TimeUnit.SECONDS.toMinutes(sec) % TimeUnit.HOURS.toMinutes(1)
                            )

                            val departureDate = itSegments.departure.at
                            val arrivalDate = itSegments.arrival.at

                            val parser =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            val formatterHour =
                                SimpleDateFormat("HH:mm", Locale.getDefault())
                            val formatterDate =
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val dateDepartFormat =
                                formatterDate.format(parser.parse(departureDate)!!)
                            val dateArrivalFormat =
                                formatterDate.format(parser.parse(arrivalDate)!!)
                            val heureDepartFormat =
                                formatterHour.format(parser.parse(departureDate)!!)
                            val heureArrivalFormat =
                                formatterHour.format(parser.parse(arrivalDate)!!)


                            run loop@{
                                rows.map { itMap ->
                                    itMap.map {
                                        if (it.value == itSegments.carrierCode) {
                                            carrierName = itMap["name"].toString()
                                            carrierCodeLogo = itMap["2char_code"].toString()
                                            return@loop
                                        }
                                    }
                                }
                            }

                            val uuid = itSegments.carrierCode + itSegments.number

                            val flight = Flight(
                                id,
                                itFlight.id.toInt(),
                                itSegments.id.toInt(),
                                itFlight.price.grandTotal,
                                itFlight.travelerPricings[0].price.total,
                                dateDepartFormat,
                                heureDepartFormat,
                                dateArrivalFormat,
                                heureArrivalFormat,
                                dureeFormat,
                                itSegments.departure.iataCode,
                                itSegments.arrival.iataCode,
                                itSegments.carrierCode,
                                carrierCodeLogo,
                                carrierName,
                                nbEscales,
                                retour,
                                false,
                                uuid
                            )
                            uuidSomme += uuid // Somme des uuid d'un même itinéraire
                            listFlightAdd.add(flight)
                            flightsListSimple?.add(flight)

                            id += 1
                        }
                        retour += 1
                    }
                    // modifie l'uuid pour mettre celui commun à l'itinéraire
                    listFlightAdd.map { itListF ->
                        itListF.uuid = uuidSomme
                        flightDao?.addFlight(itListF)
                    }
                    flightsList.add(listFlightAdd)

                }
                // Si au moins un vol de trouvé
                if (!flightsList.isNullOrEmpty()) {
                    layoutNoFlightAvailable.visibility = View.GONE
                    flightsAdapter = FlightsAdapter(flightsList, requireView())
                    mergeAdapter.addAdapter(0, flightsAdapter!!)
                }
                flightsDone = true
                searchCompleted(requireView())
            }
        }

    }

    private fun searchHotels(
        searchCity: String,
        nbHot: Int,
        dateDepart: String,
        dateArrivee: String,
        priceMinChosen: String,
        priceMaxChosen: String
    ) {

        runBlocking {

            val equipementsCsv = resources.openRawResource(R.raw.equipements)
            val listEquipements: List<Map<String, String>> =
                csvReader().readAllWithHeader(equipementsCsv)


            listEquipements.map { itMap ->
                val amenityCode = itMap["amenity_code"].toString()
                val amenityName = itMap["amenity_name"].toString()
                val listEquipement: List<String> =
                    listOf(amenityCode, amenityName)
                listEquipementFormatted.add(listEquipement)

            }
        }

        runBlocking {

            listHotels?.clear()
            listFavorisHotels.clear()
            hotelDaoSearch?.deleteHotels()
            val bddHotels = hotelDaoSaved?.getHotels()

            GlobalScope.launch {
                val hotelOffersSearches: Array<HotelOffer>
                //     if (dateArrivee != "") {
                hotelOffersSearches = try {
                    amadeus.shopping.hotelOffers[Params
                        .with("cityCode", searchCity)
                        .and("priceRange", "$priceMinChosen-$priceMaxChosen")
                        .and("checkInDate", dateDepart)
                        .and("checkOutDate", dateArrivee)
                        .and("currency", "EUR")
                        .and("lang", "FR")]

                } catch (e: Exception) {
                    Log.d("Error", e.toString())
                    emptyArray()
                }
//                } else {
//                    try {
//                        hotelOffersSearches = amadeus.shopping.hotelOffers[Params
//                            .with("cityCode", searchCity)
//                            .and("priceRange", "$priceMinChosen-$priceMaxChosen")
//                            .and("checkInDate", dateDepart)
//                            .and("currency", "EUR")
//                            .and("lang", "FR")]
//
//                    } catch (e: Exception) {
//                        Log.d("Error", e.toString())
//                        return@launch
//                    }
//                }
                val hotelNb: MutableList<HotelOffer>? = mutableListOf()

                try {
                    val nbH =
                        if (nbHot > hotelOffersSearches.size) hotelOffersSearches.size else nbHot
                    for (h in 1..nbH) {
                        hotelNb?.add(hotelOffersSearches[h - 1])
                    }
                } catch (e: Exception) {
                    Log.d("ErrorFilter", e.toString())
                }
                withContext(Dispatchers.Main) {
                    //permet d'appeler le block sur chacun des éléments d'une collection (== boucle for)
                    hotelNb?.map { itHotelOffer ->
                        var favoris = false
                        val idHotel = itHotelOffer.hotel.hotelId

                        val name = itHotelOffer.hotel.name

                        val description = if (itHotelOffer.hotel.description == null) {
                            "Description non disponible"
                        } else {
                            itHotelOffer.hotel.description.text
                        }

                        val rate = itHotelOffer.hotel.rating

                        val uri: String = if (itHotelOffer.hotel.media == null) {
                            "0"
                        } else {
                            itHotelOffer.hotel.media[0].uri
                        }

                        val adresse: MutableList<String> = mutableListOf()
                        val geocoder = Geocoder(activity)
                        try {
                            val adresseList: MutableList<Address> = geocoder.getFromLocation(
                                itHotelOffer.hotel.latitude,
                                itHotelOffer.hotel.longitude,
                                1
                            )
                            adresseList.map {
                                adresse.add(it.featureName)
                                adresse.add(it.thoroughfare)
                                adresse.add(it.postalCode)
                                adresse.add(it.locality)
                                adresse.add(it.countryName)
                            }
                        } catch (e: Exception) {
                            Log.d("ErrorGeoCoder", e.toString())
                            adresse.add("featureName")
                            adresse.add("thoroughfare")
                            adresse.add("postalCode")
                            adresse.add("locality")
                            adresse.add("countryName")
                        }


                        val telephone: String = if (itHotelOffer.hotel.contact == null) {
                            "Téléphone non disponible"
                        } else {
                            itHotelOffer.hotel.contact.phone
                        }

                        val latitude = itHotelOffer.hotel.latitude
                        val longitude = itHotelOffer.hotel.longitude

                        var price: Double = -1.0
                        itHotelOffer.offers.map {
                            if (price < 0) {
                                price = it.price.total.toDouble()
                            } else if (price > it.price.total.toDouble()) {
                                price = it.price.total.toDouble()
                            }
                        }
                        val equipements = mutableListOf<String>()
                        if (!itHotelOffer.hotel.amenities.isNullOrEmpty()) {
                            itHotelOffer.hotel.amenities.forEach { itAmenity ->
                                listEquipementFormatted.forEach {
                                    if (itAmenity == it[0]) {
                                        equipements.add(it[1])
                                    }
                                }
                            }
                        }

                        val listOfferId: MutableList<String> = mutableListOf()
                        itHotelOffer.offers.map {
                            val offerId = it.id
                            listOfferId.add(offerId)
                        }


                        var matchBdd = false
                        bddHotels?.forEach {
                            if (it.hotelId == idHotel.toInt()) {
                                listFavorisHotels.add(true)
                                matchBdd = true
                                favoris = true
                            }
                        }
                        if (!matchBdd) {
                            listFavorisHotels.add(false)
                        }

                        val hotel = Hotel(
                            0,
                            idHotel.toInt(),
                            name,
                            description,
                            rate,
                            uri,
                            adresse,
                            latitude,
                            longitude,
                            price.toString(),
                            equipements
                        )
                        runBlocking {
                            listHotels?.add(hotel)
                            hotelDaoSearch?.addHotel(hotel)
                        }
                    }

                    if (!listHotels.isNullOrEmpty()) {
                        hotelsAdapter = HotelsAdapter(listHotels!!, listFavorisHotels,mutableListOf(),"","")
                        try {
                            mergeAdapter.addAdapter(1, hotelsAdapter!!)
                        } catch (e: Exception) {
                            mergeAdapter.addAdapter(hotelsAdapter!!)
                        }
                    }
                    hotelsDone = true
                    searchCompleted(requireView())
                }
            }
        }

    }


    private fun searchActivities(
        searchCity: String,
        nbAct: String,
        aller_date: String,
        retour_date: String,
        priceRange: String
    ) {
        // supression des anciens éléments (list_fav + list_activité
        runBlocking {
            activityDaoSearch?.deleteActivity()
            listFavoris.clear()
            activitiesList = emptyList()
            cityId = ""
            query = ""
        }

        val retourDateChanged: String
        if (retour_date == "") {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val c = Calendar.getInstance()
            c.time = df.parse(aller_date)!!
            c.add(Calendar.DATE, 30)
            retourDateChanged = df.format(c.time)
        } else {
            retourDateChanged = retour_date
        }

        val service = retrofit().create(ActivitybyCity::class.java)
        runBlocking {
            // Récupère les données des items du layout
            val city = cityDao?.getCity(searchCity)
            if (city != null) {
                cityId = city.id.toString()
            } else {
                query = searchCity
            }

            categories =
                listeCatActive(cat_museum, cat_food, cat_night, cat_fun, cat_other, cat_sport)

            //lancement de la requête api
            val result = service.listAct(
                query,
                "AUTO",
                "AUTO",
                "relevance-city",
                cityId,
                priceRange,
                categories,
                nbAct,
                aller_date,
                retourDateChanged,
                lang,
                monnaie
            )


            val listActivitiesBdd = activityDaoSaved?.getActivity()
            result.data.map {
                val titre = it.title
                var matchBdd = false
                // vérification pour le bouton favoris
                listActivitiesBdd?.forEach { itBdd ->
                    if (itBdd.title == titre) {
                        listFavoris.add(true)
                        matchBdd = true
                    }
                }
                if (!matchBdd) {
                    listFavoris.add(false)
                }

                val listCat = it.categories.map { itCat ->
                    itCat.name
                }

                val activity = Activity(
                    it.uuid,
                    it.title,
                    it.city.name,
                    it.cover_image_url,
                    it.retail_price.formatted_iso_value,
                    it.operational_days,
                    it.reviews_avg,
                    listCat,
                    it.url,
                    it.top_seller,
                    it.must_see,
                    it.description,
                    it.about,
                    it.latitude,
                    it.longitude
                )
                activityDaoSearch?.addActivity(activity)

            }
            activitiesList = activityDaoSearch?.getActivity()

            if (!activitiesList.isNullOrEmpty()) {
                activityAdapter =
                    ActivityAdapterGlobal(activitiesList!!.toMutableList(), listFavoris)
                mergeAdapter.addAdapter(activityAdapter!!)
            }

            activitiesDone = true
            searchCompleted(requireView())
        }
    }

    // vide textView si non choisi dans la liste
    private fun clearFocusAutoTextView(autoCompleteTextView: AutoCompleteTextView) {
        autoCompleteTextView.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (!b) {
                // on focus off
                val str: String = autoCompleteTextView.text.toString()
                val listAdapter: ListAdapter = autoCompleteTextView.adapter
                for (i in 0 until listAdapter.count) {
                    val temp: String = listAdapter.getItem(i).toString()
                    if (str.compareTo(temp) == 0) {
                        return@OnFocusChangeListener
                    }
                }
                autoCompleteTextView.setText("")
            }
        }
    }

    // affichage du calendrier aller-retour
    private fun rangeDatePickerPrimeCalendar() {
        val rangeDaysPickCallback = RangeDaysPickCallback { startDate, endDate ->
            val parser =
                SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val formatterDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedStartDate =
                formatterDate.format(parser.parse(startDate.shortDateString)!!)
            val parsedEndDate =
                formatterDate.format(parser.parse(endDate.shortDateString)!!)
            aller_date.setText(parsedStartDate)
            return_date.setText(parsedEndDate)
        }

        val today = CivilCalendar()

        if (aller_date.text.toString() != "" && return_date.text.toString() != "") {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calStart = CivilCalendar()
            calStart.timeInMillis = df.parse(aller_date.text.toString())!!.time
            val calEnd = CivilCalendar()
            calEnd.timeInMillis = df.parse(return_date.text.toString())!!.time

            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .initiallyPickedRangeDays(calStart, calEnd)
                .firstDayOfWeek(Calendar.MONDAY)
                .minPossibleDate(today)
                .build()

            datePickerT.show(childFragmentManager, "PrimeDatePickerBottomSheet")
        } else {
            val datePickerT = PrimeDatePicker.dialogWith(today)
                .pickRangeDays(rangeDaysPickCallback)
                .firstDayOfWeek(Calendar.MONDAY)
                .minPossibleDate(today)
                .build()

            datePickerT.show(childFragmentManager, "PrimeDatePickerBottomSheet")
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

//    private fun Activity.hideKeyboard() {
//        hideKeyboard(currentFocus ?: View(this))
//    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(AppActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun listenerBouton(bt: Button, context: Context): Button {
        bt.setOnClickListener {
            if (bt.isActivated) {
                bt.isActivated = false
                bt.backgroundTintList =
                    context.resources!!.getColorStateList(R.color.white)
            } else {
                bt.isActivated = true
                bt.backgroundTintList =
                    context.resources!!.getColorStateList(R.color.butn_pressed)
            }
        }

        return bt

    }

    private fun listeCatActive(
        bt_musee: Button,
        bt_food: Button,
        bt_night: Button,
        bt_fun: Button,
        bt_other: Button,
        bt_sport: Button
    ): String {
        var string = ""
        var premierItem = true

        if (bt_musee.isActivated) {
            if (premierItem) {
                string += "arts-culture"
                premierItem = false
            } else {
                string += ",arts-culture"
            }
        }
        if (bt_food.isActivated) {
            if (premierItem) {
                string += "food-wine"
                premierItem = false
            } else {
                string += ",food_wine"
            }
        }
        if (bt_night.isActivated) {
            if (premierItem) {
                string += "nightlife"
                premierItem = false
            } else {
                string += ",nightlife"
            }
        }
        if (bt_fun.isActivated) {
            if (premierItem) {
                string += "entertainment"
                premierItem = false
            } else {
                string += ",entertainement"
            }
        }
        if (bt_other.isActivated) {
            if (premierItem) {
                string += "sightseeing"
                premierItem = true
            } else {
                string += ",sightseeing"
            }
        }
        if (bt_sport.isActivated) {
            string += if (premierItem) {
                //string += "adventure2Csports"
                "sports"
            } else {
                ",adventure,sports"
            }
        }
        return string
    }
}
