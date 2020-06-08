package com.example.tripin.profil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tripin.MainActivity
import com.example.tripin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d("erreur", "ca marche bien")

        register_button_registration.setOnClickListener {
            performRegister()
        }

        account_text_registration.setOnClickListener {
           finish()
        }
        photo_profil_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            select_photo_circleview.setImageBitmap(bitmap)
            photo_profil_button.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            photo_profil_button.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister(){
        val email = mail_edit_registration.text.toString()
        val password = password_edit_registration.text.toString()
        val pseudo = pseudo_edit_registration.text.toString()
        if(email.isEmpty() || password.isEmpty() || pseudo.isEmpty()){
            //return@setOnClickListener
            Toast.makeText(this, "Veuillez renseigner tous les champs", Toast.LENGTH_SHORT).show()
            return
        }
        if(selectedPhotoUri == null) {
            Toast.makeText(this, "Veuillez choisir une photo", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful)return@addOnCompleteListener
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Le compte n'a pas pu être créé", Toast.LENGTH_SHORT).show()
            }
    }

    var selectedPhotoUri : Uri? = null

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri == null)return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "La photo n'a pas pu être sélectionnée", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase(ProfileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(
            uid,
            pseudo_edit_registration.text.toString(),
            ProfileImageUrl
        )

        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Vous êtes bien inscrit", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("login", "login")
                finish()
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Le compte n'a pas pu être créé", Toast.LENGTH_SHORT).show()
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
}

class User (val uid : String, val username : String, val profilImageUrl : String){
    constructor() : this("", "", "")
}
