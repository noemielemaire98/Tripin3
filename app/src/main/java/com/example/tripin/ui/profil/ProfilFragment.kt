package com.example.tripin.ui.profil

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.tripin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text


class ProfilFragment : Fragment() {

    private lateinit var profilViewModel: ProfilViewModel
    private lateinit var username: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        profilViewModel =
                ViewModelProviders.of(this).get(ProfilViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profil, container, false)

        var uid = FirebaseAuth.getInstance().uid    //not the appropriate way to switch value
        val profilbutton: Button = root.findViewById(R.id.auth_button_profil)
        val signoutbutton: Button = root.findViewById(R.id.signout_button_profil)
        username = root.findViewById(R.id.username_profil)

        profilbutton.setOnClickListener { view ->
            if (uid != null) {
                Toast.makeText(this.context,
                    "Vous êtes déjà connecté",
                    Toast.LENGTH_SHORT)
                    .show()
                //makeText(this, "Vous êtes déjà connecté", Toast.LENGTH_SHORT).show()
            }
            if (uid == null) {
                val intent = Intent(this.context, LoginActivity::class.java)
                startActivity(intent)
                true
            }
        }

        signoutbutton.setOnClickListener {
            if (uid != null) {
                FirebaseAuth.getInstance().signOut()
                uid = null
                Toast.makeText(this.context,
                    "Vous êtes déconnecté",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            if (uid == null) {
                Toast.makeText(
                    this.context,
                    "Vous ne pouvez pas vous déconnectez, vous n'êtes pas connecté",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val contact: TextView = root.findViewById(R.id.textview_app_contact)
        val feedback: TextView = root.findViewById(R.id.textview_app_feedback)
        val sharing: TextView = root.findViewById(R.id.textview_app_sharing)
        val stars: TextView = root.findViewById(R.id.textview_app_stars)

        contact.setOnClickListener {
            val intent = Intent(this.context, ContactActivity::class.java)
            startActivity(intent)
        }

        feedback.setOnClickListener {
            val intent = Intent(this.context, FeedbackActivity::class.java)
            startActivity(intent)
        }

        sharing.setOnClickListener {
            //val intent = Intent(this.context, SharingActivity::class.java)
            //startActivity(intent)
            val message = ""
            val intent = Intent()
            intent.action =  Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"

            startActivity(Intent.createChooser(intent, "Partagez avec :"))
        }

        stars.setOnClickListener {
            val intent = Intent(this.context, StarsActivity::class.java)
            startActivity(intent)
        }
        return root
    }
}
