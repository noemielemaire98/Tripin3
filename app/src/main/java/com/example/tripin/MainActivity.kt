package com.example.tripin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val navView: BottomNavigationView = findViewById(R.id.nav_view)


        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_find,
                R.id.navigation_trip,
                R.id.navigation_saved,
                R.id.navigation_profil
            )
        )

        val login = intent.getStringExtra("login")

        if(login == "login") {
            navController.navigate(R.id.navigation_profil)
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val switchView = intent.getIntExtra("switchView", 0)
        val id = intent.getIntExtra("id",0)

        if ( switchView == 1 || switchView == 2 || switchView == 3 || switchView == 4 ) {
            val bundle = bundleOf("switchView" to switchView, "id" to id)

            navController.navigate(R.id.navigation_find, bundle)
        }
    }
}

