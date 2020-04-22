package com.example.tripin.ui.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tripin.LoginActivity
import com.example.tripin.R
import com.google.firebase.auth.FirebaseAuth

class ProfilFragment : Fragment() {

    private lateinit var profilViewModel: ProfilViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        profilViewModel = ViewModelProvider(this).get(ProfilViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_profil, container, false)

        var uid = FirebaseAuth.getInstance().uid    //not the appropriate way to switch value
        val profilbutton: Button = root.findViewById(R.id.auth_button_profil)

        profilbutton.setOnClickListener {view ->

            if(uid != null){
                Toast.makeText(this.context, "Vous êtes déjà connecté", Toast.LENGTH_SHORT).show()
                //makeText(this, "Vous êtes déjà connecté", Toast.LENGTH_SHORT).show()
            }
            if(uid == null){
                val intent = Intent(this.context, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        val signoutbutton: Button = root.findViewById(R.id.signout_button_profil)

        signoutbutton.setOnClickListener {
            if(uid != null){
                FirebaseAuth.getInstance().signOut()
                uid = null
                Toast.makeText(this.context, "Vous êtes déconnecté", Toast.LENGTH_SHORT).show()
            }
            if(uid == null){
                Toast.makeText(this.context, "Vous ne pouvez pas vous déconnectez, vous n'êtes pas connecté", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

}
