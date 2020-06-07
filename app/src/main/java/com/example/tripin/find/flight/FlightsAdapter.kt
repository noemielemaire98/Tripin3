package com.example.tripin.find.flight

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.RangeDaysPickCallback
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.data.VoyageDao
import com.example.tripin.model.Flight
import com.example.tripin.model.Voyage
import kotlinx.android.synthetic.main.activity_saved_flight.view.*
import kotlinx.android.synthetic.main.createvoyage_popup.view.*
import kotlinx.android.synthetic.main.flights_view.view.*
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class FlightsAdapter(private val flightsList: MutableList<MutableList<Flight>>, private val viewFragment : View) :
    RecyclerView.Adapter<FlightsAdapter.FlightsViewHolder>() {

    private var voyageDaoSaved: VoyageDao? = null
    private var listFlightsBdd: MutableList<Flight>? = null
    private var flightDaoSaved: FlightDao? = null
    private lateinit var context: Context
    private var dateDebut = ""
    private var dateFin = ""
    private var destination = ""
    private var budget = ""
    var image = ""

    class FlightsViewHolder(val flightsView: View) : RecyclerView.ViewHolder(flightsView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightsViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.flights_view, parent, false)

        context = parent.context

        val databaseSaved = Room.databaseBuilder(context, AppDatabase::class.java, "savedDatabase")
            .build()

        flightDaoSaved = databaseSaved.getFlightDao()
        voyageDaoSaved = databaseSaved.getVoyageDao()

        runBlocking {
            listFlightsBdd = flightDaoSaved?.getFlights()
        }
        return FlightsViewHolder(view)
    }

    override fun getItemCount(): Int = flightsList.size


    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: FlightsViewHolder, position: Int) {
        val view = holder.flightsView

        var favoris = false
        flightsList.map { itListFlight ->

            val flights = flightsList[position]
            var pot = 0
            var returnTravel = 0


            itListFlight.map {
                if (pot != flights.size) {
                    val itFlight = flights[pot]
                    if (flights[pot].retour == 0 && (pot == 0
                                || (flights[pot - 1].travelId == flights[pot].travelId
                                && flights[pot - 1].dureeVol != flights[pot].dureeVol))
                    ) {

                        view.flights_price_textview.text = "${itFlight.prixTotal} €"
                        view.aller_date.text = "${itFlight.heureDepart} - "
                        view.flights_date_textview.text = itFlight.dateDepart

                        view.lieuDepart_aller.text = "${itFlight.lieuDepart} - "

                        view.compagnie_aller.text =
                            if (itFlight.carrierName != "") itFlight.carrierName else itFlight.carrierCode

                        view.duree.text = itFlight.dureeVol
                        view.flights_direct_textview.text =
                            if (itFlight.nbEscales == 1) "${itFlight.nbEscales} escale" else if (itFlight.nbEscales > 1) "${itFlight.nbEscales} escales" else "Direct"

                        var ims: InputStream? = null
                        try {
                            ims = view.context.assets.open(
                                "optd_airlines_websites_logos/${itFlight.carrierCodeLogo.toLowerCase(
                                    Locale.ROOT
                                )}.png"
                            )
                            val d = Drawable.createFromStream(ims, null)
                            view.flights_imageView.setImageDrawable(d)
                        } catch (ex: IOException) {
                            //file does not exist
                            view.flights_imageView.setImageResource(R.drawable.ic_flight_black_24dp)
                        } finally {
                            ims?.close()
                        }

                    } else if (flights[pot].retour == 1 && flights[pot - 1].retour == 0) {

                        returnTravel = pot

                        view.heureDepart_retour.text = "${itFlight.heureDepart} - "
                        view.flights_date_return_textview.text = itFlight.dateDepart

                        view.lieuDepart_retour.text = "${itFlight.lieuDepart} - "

                        view.compagnie_retour.text =
                            if (itFlight.carrierName != "") itFlight.carrierName else itFlight.carrierCode

                        view.duree_retour.text = itFlight.dureeVol
                        view.flights_direct_return_textview.text =
                            if (itFlight.nbEscales == 1) "${itFlight.nbEscales} escale" else if (itFlight.nbEscales > 1) "${itFlight.nbEscales} escales" else "Direct"

                        var ims: InputStream? = null
                        try {
                            ims = view.context.assets.open(
                                "optd_airlines_websites_logos/${itFlight.carrierCodeLogo.toLowerCase(
                                    Locale.ROOT
                                )}.png"
                            )
                            val d = Drawable.createFromStream(ims, null)
                            view.flights_imageView_return.setImageDrawable(d)
                        } catch (ex: IOException) {
                            //file does not exist
                            view.flights_imageView_return.setImageResource(R.drawable.ic_flight_black_24dp)
                        } finally {
                            ims?.close()
                        }

                    }
                    if (flights[pot].retour == 0 && (pot == flights.size - 1 || flights[pot + 1].retour == 1)) {
                        view.heureArrivee_aller.text =
                            itFlight.heureArrivee
                        view.lieuArrivee_aller.text = "${itFlight.lieuArrivee}, "

                        val parsedDateDepart =
                            SimpleDateFormat("dd/MM/yyyy").parse(flights[0].dateDepart)
                        val parsedDateArrivee =
                            SimpleDateFormat("dd/MM/yyyy").parse(itFlight.dateArrivee)

                        val diff = parsedDateArrivee!!.time.minus(parsedDateDepart!!.time)

                        val compare = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                        if (compare.toInt() != 0) {
                            view.heureArriveeAdd_aller.text = "+${compare}"
                            view.heureArriveeAdd_aller.setTextColor(
                                ContextCompat
                                    .getColor(
                                        view.heureArriveeAdd_aller.context,
                                        R.color.colorAccent
                                    )
                            )
                            view.heureArrivee_aller.setTextColor(
                                ContextCompat
                                    .getColor(
                                        view.heureArrivee_aller.context,
                                        R.color.colorAccent
                                    )
                            )
                        }
                        if (pot == flights.size - 1) view.layout_retour.visibility =
                            View.GONE
                    }
                    if (flights[pot].retour == 1 && pot == flights.size - 1) {
                        view.heureArrivee_retour.text =
                            itFlight.heureArrivee
                        view.lieuArrivee_retour.text = "${itFlight.lieuArrivee}, "

                        val parsedDateDepart =
                            SimpleDateFormat("dd/MM/yyyy").parse(flights[returnTravel].dateDepart)
                        val parsedDateArrivee =
                            SimpleDateFormat("dd/MM/yyyy").parse(itFlight.dateArrivee)

                        val diff = parsedDateArrivee!!.time.minus(parsedDateDepart!!.time)

                        val compare = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                        if (compare.toInt() != 0) {
                            view.heureArriveeAdd_retour.text = "+${compare}"
                            view.heureArriveeAdd_retour.setTextColor(
                                ContextCompat
                                    .getColor(
                                        view.heureArriveeAdd_retour.context,
                                        R.color.colorAccent
                                    )
                            )
                            view.heureArrivee_retour.setTextColor(
                                ContextCompat
                                    .getColor(
                                        view.heureArrivee_retour.context,
                                        R.color.colorAccent
                                    )
                            )
                        }
                    }
                    pot += 1
                }
            }

//            view.setOnClickListener { itView ->
//                val intent = Intent(itView.context, DetailFlights::class.java)
//                intent.putExtra("flights", flights.toTypedArray())
//                itView.context.startActivity(intent)
//            }

            listFlightsBdd?.map { itList ->

                if (itList.uuid == flights[0].uuid) {
                    favoris = true
                }
            }

            if (flights[0].favoris || favoris) {
                view.fab_favFlight.setImageResource(R.drawable.ic_favorite_black_24dp)
                favoris = true

            } else if (!flights[0].favoris || !favoris) {
                view.fab_favFlight.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                favoris = false

            }

            view.fab_favFlight.setOnClickListener {

                if (!favoris) {

                    val row = listFlightsBdd?.size
                    val travelId =
                        if (!listFlightsBdd.isNullOrEmpty()) listFlightsBdd!![row!! - 1].travelId + 1 else 1
                    var id =
                        if (!listFlightsBdd.isNullOrEmpty()) listFlightsBdd!![row!! - 1].id + 1 else 1
                    flights.map {
                        it.favoris = true
                        runBlocking {
                            val flight = Flight(
                                id,
                                travelId,
                                it.SegmentId,
                                it.prixTotal,
                                it.prixParPassager,
                                it.dateDepart,
                                it.heureDepart,
                                it.dateArrivee,
                                it.heureArrivee,
                                it.dureeVol,
                                it.lieuDepart,
                                it.lieuArrivee,
                                it.carrierCode,
                                it.carrierCodeLogo,
                                it.carrierName,
                                it.nbEscales,
                                it.retour,
                                it.favoris,
                                it.uuid
                            )
                            flightDaoSaved?.addFlight(flight)
                            id += 1
                        }
                    }
                    favoris = true
                    view.fab_favFlight.setImageResource(R.drawable.ic_favorite_black_24dp)
                    Toast.makeText(
                        context,
                        "Le vol a bien été ajouté aux favoris",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (favoris) {

                    flights.map {
                        runBlocking {
                            flightDaoSaved?.deleteFlight(it.uuid)
                        }
                    }

                    val fragmentFavorisViewPager = try {
                        // https://stackoverflow.com/a/54829516/13289762
                        (context as MainActivity).supportFragmentManager.fragments[0].childFragmentManager.fragments[0]?.childFragmentManager?.fragments
                            ?.get(1)?.javaClass?.simpleName
                    } catch (ex: Exception) {
                        ""
                    }

                    if (fragmentFavorisViewPager == "SavedFlightFragment") {
                        flightsList.remove(flights)

                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, itemCount)

                        if (flightsList.isNullOrEmpty()) {
                            viewFragment.layoutNoSavedFlight.visibility = View.VISIBLE
                        }
                    }

                    favoris = false

                    view.fab_favFlight.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    Toast.makeText(
                        context,
                        "Le vol a bien été supprimé des favoris",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                runBlocking {
                    listFlightsBdd = flightDaoSaved?.getFlights()
                }
            }

            //AU CLIC SUR LE BOUTON AJOUT A UN VOYAGE
            view.fab_addFlight.setOnClickListener {
                val listCheckeditems = ArrayList<Boolean>()
                val listVoyage: ArrayList<String> = arrayListOf()
                runBlocking {
                    val voyage = voyageDaoSaved?.getVoyage()
                    voyage?.map {
                        var dejaAjoute = false
                        listVoyage.add(it.titre)
                        it.list_flights?.map { itF ->
                            if (itF.uuid == flights[0].uuid) {
                                dejaAjoute = true
                            }
                        }
                        listCheckeditems.add(dejaAjoute)
                    }
                }
                val plusdialog = AlertDialog.Builder(context)
                plusdialog.setTitle("Dossier de voyage")
                val listChoix = arrayListOf<String>()

                if (listVoyage.isNullOrEmpty()) {
                    plusdialog.setMessage("Vous n'avez constitué aucun dossier de voyage, cliquez sur créer.")
                } else {
                    hasVoyage(listVoyage, listCheckeditems, plusdialog, listChoix, flights)
                }

                plusdialog.setPositiveButton(android.R.string.ok) { _, _ ->
                    plusdialog.show().dismiss()
                }


                plusdialog.setNeutralButton("Créer") { _, _ ->


                    val createDialog = AlertDialog.Builder(context)

                    val viewPop = LayoutInflater.from(context).inflate(
                        R.layout.createvoyage_popup,
                        view.findViewById(android.R.id.content),
                        false
                    )
                    val editText = viewPop.findViewById<EditText>(R.id.et_date)
                    val okButton = viewPop.findViewById<Button>(R.id.bt_ok)
                    val returnButton = viewPop.findViewById<Button>(R.id.bt_retour)
                    val editTitre = viewPop.findViewById<EditText>(R.id.et_titre)
                    val editDate = viewPop.findViewById<EditText>(R.id.et_date)
                    createDialog.setView(viewPop)
                    createDialog.setTitle("Créer")
                    val alert = createDialog.show()
                    editText.setOnClickListener {
                        rangeDatePickerPrimeCalendar(editText)
                    }


                    okButton.setOnClickListener {
                        var exist = false
                        listVoyage.map { itL ->
                            if (editTitre.text.toString() == itL) {
                                exist = true
                            }
                        }
                        if (exist) {
                            Toast.makeText(
                                context,
                                "Ce nom de voyage existe déjà, veuillez en choisir un autre",
                                Toast.LENGTH_SHORT
                            ).show()
                            editTitre.setText("")
                        } else if (editTitre.text.isNotEmpty() && editDate.text.isNotEmpty()) {
                            val voyage = Voyage(
                                0,
                                viewPop.et_titre.text.toString(),
                                dateDebut,
                                dateFin,
                                image,
                                viewPop.et_nb_voyageur.selectedItem.toString().toInt(),
                                emptyList(),
                                emptyList(),
                                emptyList(),
                                destination,
                                budget
                            )
                            runBlocking {
                                voyageDaoSaved?.addVoyage(voyage)
                            }
                            listVoyage.add(viewPop.et_titre.text.toString())
                            plusdialog.show().dismiss()
                            view.fab_addFlight.performClick()
                            alert.dismiss()
                        } else {
                            Toast.makeText(
                                context,
                                "Veuillez saisir tous les champs",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    returnButton.setOnClickListener {
                        alert.dismiss()
                        plusdialog.show().dismiss()
                        view.fab_addFlight.performClick()
                    }
                }
                plusdialog.show()
            }
        }
    }

    private fun hasVoyage(
        list_voyage: ArrayList<String>,
        list_checkedItems: ArrayList<Boolean>,
        plusdialog: AlertDialog.Builder,
        list_choix: ArrayList<String>,
        flights: MutableList<Flight>
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
                    val ancienneList = voyage!!.list_flights?.toMutableList()
                    ancienneList?.addAll(flights)
                    val nouvelleListe = ancienneList?.toList()
                    voyage.list_flights = nouvelleListe

                    voyageDaoSaved?.updateVoyage(voyage)
                }
                Toast.makeText(
                    context,
                    "Le vol a bien été ajouté à ${list_voyage[which]}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                list_choix.remove(list_voyage[which])
                runBlocking {
                    val voyage = voyageDaoSaved?.getVoyageByTitre(list_voyage[which])
                    val ancienneList = voyage?.list_flights?.toMutableList()
                    ancienneList?.removeAll(flights)
                    val nouvelleListe = ancienneList?.toList()
                    voyage!!.list_flights = nouvelleListe
                    voyageDaoSaved?.updateVoyage(voyage)
                }
                Toast.makeText(
                    context,
                    "Le vol a bien été supprimé de ${list_voyage[which]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
            dateDebut = parsedStartDate
            dateFin = parsedEndDate
        }

        val today = CivilCalendar()

        val datePickerT = PrimeDatePicker.dialogWith(today)
            .pickRangeDays(rangeDaysPickCallback)
            .firstDayOfWeek(Calendar.MONDAY)
            .minPossibleDate(today)
            .build()

        datePickerT.show(
            (context as MainActivity).supportFragmentManager,
            "PrimeDatePickerBottomSheet"
        )
    }
}