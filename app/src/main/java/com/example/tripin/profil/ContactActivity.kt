package com.example.tripin.profil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tripin.R
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        envoyer_button.setOnClickListener {
            var destinataire = destinataire_edit_text.text.toString().trim()
            var objet = objet_edit_text.text.toString().trim()
            var message = message_edit_text.text.toString().trim()

            sendEmail(destinataire, objet, message)
        }
    }

    private fun sendEmail(destinataire: String, objet: String, message: String){
        val intent = Intent(Intent.ACTION_SEND)

        intent.data = Uri.parse("mailto:")
        intent.type = "text/plain"

        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(destinataire))
        intent.putExtra(Intent.EXTRA_SUBJECT, objet)
        intent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(intent, "Selectionner l'application de votre choix"))
        }catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
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
