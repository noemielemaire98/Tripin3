package com.example.tripin.find.hotel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripin.R
import kotlinx.android.synthetic.main.activity_gestion_rooms.*


class RoomsGestion : AppCompatActivity() {

    private var adultsList : ArrayList<String> ?= arrayListOf()
    //private var childrenList : ArrayList<String> = arrayListOf()
    private var adultsNumber : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_rooms)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        add_room_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        add_room_number_adults.text = adultsNumber.toString()

        decrease_rooms.setOnClickListener {
            hideKeyboard()
            if (adultsNumber == 0) {
            } else {
                adultsNumber = adultsNumber.minus(1)
            }

            add_room_number_adults.text = adultsNumber.toString()
        }

        increase_rooms.setOnClickListener {
            hideKeyboard()
            if (adultsNumber == 4) {
            } else {
                adultsNumber = adultsNumber.plus(1)
            }
            add_room_number_adults.text = adultsNumber.toString()
        }

        add_room_button.setOnClickListener {
            hideKeyboard()

            val nbadults = add_room_number_adults.text.toString()
            adultsList?.add(nbadults)
            Log.d("Rooms", adultsList.toString())
            add_room_recyclerview.adapter = AddRoomAdapter(adultsList)
            adultsNumber = 0
            add_room_number_adults.text = adultsNumber.toString()


            add_rooms_ok.setOnClickListener{
                val intent = Intent(this, FindHotelFragment::class.java)

                Log.d("TestLists","1")
                intent.putExtra("listAdults", adultsList.toString())
                Log.d("TestLists",adultsList.toString())
                setResult(Activity.RESULT_OK, intent)
                Log.d("TestLists","3")
                this.finish()
                Log.d("TestLists","4")

            }
    }




    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}