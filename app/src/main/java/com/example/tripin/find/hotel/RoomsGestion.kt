package com.example.tripin.find.hotel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripin.R
import kotlinx.android.synthetic.main.activity_gestion_rooms.*


class RoomsGestion : AppCompatActivity() {

    private var adultsList : ArrayList<String> ?= arrayListOf()
    //private var childrenList : ArrayList<String> = arrayListOf()
    private var adultsNumber : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_rooms)
        adultsList = intent.getStringArrayListExtra("adultsList")

        var listEmpty = false
        if(adultsList!!.isEmpty()){
            listEmpty = true
        }
        add_room_recyclerview.adapter = AddRoomAdapter(adultsList)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        add_room_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        add_room_number_adults.text = adultsNumber.toString()

        decrease_rooms.setOnClickListener {
            hideKeyboard()
            if (adultsNumber == 1) {
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

            if(adultsList?.size == 4){
                Toast.makeText(this,"Nombre de chambres maximal atteint", Toast.LENGTH_SHORT).show()

            } else {
                val nbadults = add_room_number_adults.text.toString()
                adultsList?.add(nbadults)
                add_room_recyclerview.adapter = AddRoomAdapter(adultsList)

            }
            adultsNumber = 1
            add_room_number_adults.text = adultsNumber.toString()
    }

        add_rooms_ok.setOnClickListener{
            if(adultsList.isNullOrEmpty()){
                Toast.makeText(this, "Aucune chambre n'a été ajoutée", Toast.LENGTH_LONG ).show()
                listEmpty = true
            }else{
                listEmpty = false
            }
            val intent = Intent(this, FindHotelFragment::class.java)
            Log.d("TestLists", "Intent ${adultsList}")
            Log.d ("TestLists", "Intent ${listEmpty}")
            intent.putExtra("listEmpty", listEmpty)
            intent.putExtra("listAdults", adultsList.toString())
            setResult(Activity.RESULT_OK, intent)
            this.finish()

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

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}