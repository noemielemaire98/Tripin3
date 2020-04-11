package com.example.tripin.ui.find

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.tripin.FlightsAdapter
import com.example.tripin.R
import com.example.tripin.data.AppDatabase
import com.example.tripin.data.FlightDao
import com.example.tripin.model.Flight
import kotlinx.android.synthetic.main.fragment_find.*
import kotlinx.android.synthetic.main.fragment_find.view.*
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class FindFragment : Fragment() {

    private val apiHost = "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com"
    private val apiKey = "5f672e716bmsh702ca7444dd484cp121785jsn039c3a4937f8"
    lateinit var date: String
    var cal = Calendar.getInstance()
    private var flightDao: FlightDao? = null

    private lateinit var dashboardViewModel: FindViewModel

    val QuotesFlightsServe by lazy {
        QuotesFlightsService.create()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_find, container, false)


        var flights_recyclerview =
            view.findViewById<View>(R.id.flights_recyclerview) as RecyclerView
        flights_recyclerview.layoutManager = LinearLayoutManager(this.context)

        val startDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateStartDateInView()
            }

        val returnDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateReturnDateInView()
            }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        view.start_date?.setOnClickListener {
            var dialog = DatePickerDialog(
                requireContext(),
                startDateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }

        view.return_date?.setOnClickListener {
            var dialog = DatePickerDialog(
                requireContext(),
                returnDateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }


        view.btn_search.setOnClickListener {
//            val parsedDate = SimpleDateFormat("yyyy-MM-dd").parse(start_date.text.toString())
//            val currentDate =
//                SimpleDateFormat("yyyy-MM-dd").parse(SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time))
//            if (start_date != null && currentDate.before(parsedDate)) {
            if (start_date != null) {
                date = start_date.text.toString()
                //           Log.d("Date", date)
                beginSearch(date)
            }
        }

        view.allerType_radiogroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = activity!!.findViewById(checkedId)
                view.return_date.visibility =
                    if (view.allerType_radiogroup.checkedRadioButtonId == view.simple_button.id) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            })


        val database =
            Room.databaseBuilder(this.context!!, AppDatabase::class.java, "gestionflights")
                .build()

        flightDao = database.getFlightDao()

        return view

    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val flights = flightDao?.getFlights()
            //     val flight = Flight(1, 20.toDouble(), "DepartureDate")
            flights_recyclerview.adapter = FlightsAdapter(flights ?: emptyList())

        }
    }

    @SuppressLint("SetTextI18n")
    private fun beginSearch(date: String) {
        runBlocking {
            flightDao?.deleteFlights()
            val disposable = QuotesFlightsServe.listFlights(date, apiHost, apiKey)
            disposable.quotes.map {
                //        txt_search_price.text = "MinPrice : " + it.minPrice.toString()
                //       txt_search_direct.text = it.direct.toDirect()
                //         flights_title_textview?.text = it.minPrice.toString()
                //       flights_date_textview?.text = it.outboundLeg.departureDate.toString()

                val flight = Flight(it.quoteId, it.minPrice, date, it.direct)

                flightDao?.addFlight(flight)
                //        flights_recyclerview.adapter = FlightsAdapter(flightDao?.getFlights() ?: emptyList())


            }
            onResume()
        }
    }

    private fun updateStartDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        start_date!!.setText(sdf.format(cal.getTime()))
    }

    private fun updateReturnDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return_date!!.setText(sdf.format(cal.getTime()))
    }

}


fun Boolean.toDirect() = if (this) "Vol direct" else "Vol avec escale(s)"
