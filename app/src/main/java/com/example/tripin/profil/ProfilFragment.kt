package com.example.tripin.profil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.tripin.ContactActivity
import com.example.tripin.FeedbackActivity
import com.example.tripin.R
import com.example.tripin.StarsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_profil.*


class ProfilFragment : Fragment() {


    private lateinit var username: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profil, container, false)

        var uid = FirebaseAuth.getInstance().uid    //not the appropriate way to switch value
        val profilbutton: Button = root.findViewById(R.id.auth_button_profil)
        val signoutbutton: Button = root.findViewById(R.id.signout_button_profil)
        val username : TextView = root.findViewById(R.id.username_profil)

        if(uid != null){
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("users/$uid")
            val users = myRef.orderByKey().addChildEventListener(object: ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("toto", "$p0")
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    if(p0.key == "profilImageUrl"){
                        //Log.d("toto", "$p0, $p1")
                        //Log.d("toto", "${p0.getValue(String::class.java)}")
                        Glide.with(requireContext()).load("${p0.getValue(String::class.java)}").into(display_photo_circleview)
                    }
                    if(p0.key == "username"){
                        //Log.d("toto", "${p0.getValue(String::class.java)}")
                        username.setText("${p0.getValue(String::class.java)}")
                    }
                }
                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("Not yet implemented")
                }
            })
            //Log.d("tata", "$users")
        }


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
