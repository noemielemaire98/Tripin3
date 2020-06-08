package com.example.tripin.profil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.example.tripin.data.*
import com.example.tripin.find.IgnoreAccentsArrayAdapter
import com.example.tripin.model.Preference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_preferences.*
import kotlinx.android.synthetic.main.fragment_profil.*
import kotlinx.android.synthetic.main.fragment_profil.activity_categorie
import kotlinx.android.synthetic.main.fragment_profil.destination_categorie
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.find


class ProfilFragment : Fragment() {


    private lateinit var username: TextView
    private var preferenceDao: PreferenceDao? = null
    private var hotelDao: HotelDao? = null
    private var activityDao: ActivityDao? = null
    var ville = ""
    var destination = ""
    var budget = 100
    private lateinit var citydao: CityDao
    var list_cities_name = arrayListOf<String>()
    var au_soleil = listOf<String>("Madrid","Marrakech","Johannesburg","Buenos Aires")
    var pour_visiter = listOf<String>("Budapest","Venise","San Francisco","Berlin")
    var exotique = listOf<String>("Bangkok","Honolulu","Rio de Janeiro","Sydney")
    var decouvrir = listOf<String>("Cap Town","Chicago","Rome","Shanghai")
    var nature = listOf<String>("Cap Town","Honolulu")
    private lateinit var destinationCat  : AutoCompleteTextView
    private lateinit var edit_budget : EditText
    private lateinit var activityCat : AutoCompleteTextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profil, container, false)

        var uid = FirebaseAuth.getInstance().uid    //not the appropriate way to switch value
//        val profilbutton: Button = root.findViewById(R.id.auth_button_profil)
        val signoutbutton: Button = root.findViewById(R.id.signout_button_profil)
        val username : TextView = root.findViewById(R.id.username_profil)
        val noProfilLayout : RelativeLayout = root.findViewById(R.id.noProfil_layout)
        val connexionButton : Button = root.findViewById(R.id.connexion_button)
        val inscriptionTxt : TextView = root.findViewById(R.id.inscription_button)
        val profilLayout : LinearLayout = root.findViewById(R.id.linear_layout_profil)
        edit_budget = root.findViewById<EditText>(R.id.budget_edit_preferences)
        val bsave = root.findViewById<Button>(R.id.button_save)
        destinationCat = root.findViewById<AutoCompleteTextView>(R.id.destination_categorie)
        val listCatDestination = resources.getStringArray(R.array.catDestination)
        val adapterListCatDestination =
            IgnoreAccentsArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listCatDestination)
        destinationCat.setAdapter(adapterListCatDestination)

        activityCat = root.findViewById<AutoCompleteTextView>(R.id.activity_categorie)
        val listCatActivity = resources.getStringArray(R.array.catActivity)
        val adapterListCatActivity =
            IgnoreAccentsArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listCatActivity)
        activityCat.setAdapter(adapterListCatActivity)



        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "allpreferences"
        ).build()

        val databaseHome = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "homeDatabase"
        ).build()

        val databasesaved =
            Room.databaseBuilder(requireContext(), AppDatabase::class.java, "savedDatabase")
                .build()

        // récupération des villes possibles
        citydao = databasesaved.getCityDao()

        // ajout dans l'auto complétion texte
        runBlocking {
            val list_cities_bdd = citydao.getCity()
            list_cities_bdd.map {
                list_cities_name.add(it.name!!)
            }
        }
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list_cities_name)

        preferenceDao = database.getPreferenceDao()
        hotelDao = databaseHome.getHotelDao()
        activityDao = databaseHome.getActivityDao()

        if(uid != null){
            noProfilLayout.visibility = View.GONE
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
                        Glide.with(context as MainActivity).load("${p0.getValue(String::class.java)}").into(display_photo_circleview)
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

        }
        if(uid == null){
            noProfilLayout.visibility = View.VISIBLE
            profilLayout.visibility = View.GONE
        }

        connexionButton.setOnClickListener {
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
        }

        inscriptionTxt.setOnClickListener {
            val intent = Intent(this.context, RegistrationActivity::class.java)
            startActivity(intent)
        }

//        profilbutton.setOnClickListener { view ->
//            if (uid != null) {
//                Toast.makeText(this.context,
//                    "Vous êtes déjà connecté",
//                    Toast.LENGTH_SHORT)
//                    .show()
//                //makeText(this, "Vous êtes déjà connecté", Toast.LENGTH_SHORT).show()
//            }
//            if (uid == null) {
//                val intent = Intent(this.context, LoginActivity::class.java)
//                startActivity(intent)
//            }
//        }

        signoutbutton.setOnClickListener {
            if (uid != null) {
                FirebaseAuth.getInstance().signOut()
                uid = null
                Toast.makeText(this.context,
                    "Vous êtes déconnecté",
                    Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this.context, MainActivity::class.java)
                startActivity(intent)
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
        val feedback: Button = root.findViewById(R.id.textview_app_feedback)
        val sharing: TextView = root.findViewById(R.id.textview_app_sharing)
        val stars: TextView = root.findViewById(R.id.textview_app_stars)

        contact.setOnClickListener {
            val intent = Intent(this.context, ContactActivity::class.java)
            startActivity(intent)
        }

        feedback.setOnClickListener {
            random_destination()

            budget = edit_budget.text.toString().toInt()
            val destinationCatVal = destination_categorie.text
            val activityCatVal = activity_categorie.text
            Log.d("KLM", "choix envie : $destinationCatVal")
            Log.d("KLM", "destination $destination")
            Log.d("KLM", "budget $budget")
            if (activityCatVal.toString() == "" || edit_budget.text.toString() == "" || budget == null) {
                Toast.makeText(requireContext(), "Veuillez compléter tous les champs", Toast.LENGTH_SHORT)
                    .show()
            } else {
                runBlocking {
                    preferenceDao?.deletePreferences()

                    hotelDao?.deleteHotels()
                    activityDao?.deleteActivity()

                    val pref = Preference(
                        "",
                        destination,
                        destinationCatVal.toString(),
                        budget,
                        activityCatVal.toString()
                    )
                    preferenceDao?.addPreference(pref)
                }
                hideKeyboard()
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
           /* val intent = Intent(this.context, PreferencesActivity::class.java)
            startActivity(intent)*/
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

    override fun onResume() {
        super.onResume()
        runBlocking {
            var preference = preferenceDao!!.getPreference()
            if(preference != null){
                val list_destination = resources.getStringArray(R.array.catDestination).toMutableList()
                list_destination.remove(preference.envie)
                destinationCat.setText(preference.envie)
                val adapterListCatDestination =
                    IgnoreAccentsArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list_destination)
                destinationCat.setAdapter(adapterListCatDestination)

                val list_activity = resources.getStringArray(R.array.catActivity).toMutableList()
                list_activity.remove(preference.souhait)
                list_activity.removeAt(0)
                activityCat.setText(preference.souhait)
                val adapterListCatActivity =
                    IgnoreAccentsArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,list_activity)
                activityCat.setAdapter(adapterListCatActivity)

                edit_budget.setText(preference.budget.toString())
            }


        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun random_destination() {
        if(destination_categorie.text.toString() == "Exotique"){
            exotique = exotique.shuffled()
            destination = exotique[0]
        }else if(destination_categorie.text.toString() == "Au soleil"){
            au_soleil = au_soleil.shuffled()
            destination = au_soleil[0]
        }else if(destination_categorie.text.toString() == "Nature"){
            nature = nature.shuffled()
            destination = nature[0]
        }else if(destination_categorie.text.toString() == "A découvrir"){
            decouvrir = decouvrir.shuffled()
            destination = decouvrir[0]
        }else if(destination_categorie.text.toString() == "Pour visiter"){
            pour_visiter = pour_visiter.shuffled()
            destination = pour_visiter[0]
        }
    }

}
