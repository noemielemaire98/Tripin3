package com.example.tripin.profil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.example.tripin.R

class StarsActivity : AppCompatActivity() {

    lateinit var btn: Button
    lateinit var rb: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stars)

        btn = findViewById<View>(R.id.rating_button) as Button
        rb = findViewById<View>(R.id.rating_bar) as RatingBar
    }

    fun click (view: View){
        val valeur = rb.rating
        Toast.makeText(this, " La note est de : " + valeur, Toast.LENGTH_LONG).show()
    }
}
