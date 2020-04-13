package com.example.tripin.ui.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.tripin.RegistrationActivity
import com.example.tripin.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profil.view.*

class ProfilFragment : Fragment() {

    private lateinit var profilViewModel: ProfilViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        profilViewModel =
                ViewModelProviders.of(this).get(ProfilViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profil, container, false)

        val button: Button = root.findViewById(R.id.auth_button_profil)
        button.setOnClickListener {view ->
            val uid = FirebaseAuth.getInstance().uid
            if(uid != null){
                Toast.makeText(this.context, "Vous êtes déjà connecté", Toast.LENGTH_SHORT).show()
                //makeText(this, "Vous êtes déjà connecté", Toast.LENGTH_SHORT).show()
            }
            if(uid == null){
                val intent = Intent(this.context, RegistrationActivity::class.java)
                startActivity(intent)
                true
            }
        }
        return root
    }

}
