package com.example.perdpaslenor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Timer
import java.util.TimerTask


class Authentification : AppCompatActivity() {
    private var boutonconf: Button? = null
    private var editcode : EditText? = null
    private var nbr: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentification)

        val phoneTO = intent.getStringExtra("phoneTO")
        val codeAUTH = intent.getStringExtra("codeAUTH")
        val ME = intent.getStringExtra("ME")
        val Genre = intent.getStringExtra("Genre")

        if(Genre == "parent"){
            IHMPARENT()
        }else if(Genre == "enfant"){
            IHMENFANT()
        }

        boutonconf = findViewById<View>(R.id.buttonconfirmation) as Button?

        Toast.makeText(this, "Un code authentification a été envoyer", Toast.LENGTH_SHORT).show();

        Chekup(ME, phoneTO ,codeAUTH)

        val timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this@Authentification,
                        "Il vous reste 1 minute pour rentrer le code reçu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, 60000)

        timer.schedule(object : TimerTask() {
            override fun run() {
                TempsFIN()
            }
        }, 120000) // 120000 millisecondes = 2 minutes

        //Regarde si la permission de la localisation est accordée
        if (checkLocationPermission()) {
            val intent = Intent(this, BackgroundChildrenService::class.java)
            startService(intent)
        } else {
            // Demande la localisation si elle n'est pas active
            requestLocationPermission()
        }

        boutonconf!!.setOnClickListener { Parents(ME,phoneTO,codeAUTH) }
    }

    private fun TempsFIN(){
        val intent = Intent(this, Connexion::class.java)
        startActivity(intent)
        finish()
    }

    private fun Parents(ME : String?, phoneTO: String?, codeAUTH : String?){
        editcode = findViewById(R.id.editionAUTH)
        val etexte: String = editcode?.text.toString()
        if(nbr > 0) {
            if (codeAUTH == etexte) {
                val internetPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_GRANTED

                if(internetPermission) {
                    // Établis la connexion avec la base de données
                    val db = FirebaseFirestore.getInstance()

                    // Créez un objet de type Map pour stocker vos données
                    val user = hashMapOf(
                        "numeroEnfant" to phoneTO,
                        "numeroParent" to ME
                    )

                    // Ajoutez les données à votre collection "utilisateurs"
                    db.collection("user")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            // L'ajout des données a réussi
                            Toast.makeText(
                                this,
                                "L'ajout des données a réussi",
                                Toast.LENGTH_SHORT
                            ).show();
                            IHMPARENT()
                        }
                        .addOnFailureListener { e ->
                            // L'ajout des données a échoué
                            Toast.makeText(
                                this,
                                "L'ajout des données a échoué",
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                }else{
                    Toast.makeText(
                        this,
                        "Veuillez autoriser la connexion à internet",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            } else {
                Toast.makeText(
                    this,
                    "Le code d'authentification ne correspond pas (il vous reste " + nbr + " essaie avant que le code ne soit plus valide)",
                    Toast.LENGTH_SHORT
                ).show();
                nbr -= 1
            }
        }else{
            val intent = Intent(this, Connexion::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun Chekup(ME : String?, phoneTO: String?, codeAUTH : String?){
        var error = false
        if(phoneTO ==  null){
            error = true
        }
        if(codeAUTH ==  null){
            error = true
        }
        if(ME ==  null){
            error = true
        }

        if(error == true){
            Toast.makeText(this, "Error réception de données", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    // Cette méthode est automatiquement appellée par le système après qu'il ait accepté ou non la permission de la localisation
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Regarde si c'est la demande pour la permission de la localisation qui a été traitée
        if (requestCode == 1) {

            // Regarde si la permission est accordée
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée
                Log.d("test", "grantResult")

                val intentBgChild = Intent(this, BackgroundChildrenService::class.java)

                startService(intentBgChild)
            } else {
                // Permission refusé, re-demande la permission
                requestLocationPermission()
            }
        }
    }

    private fun IHMPARENT(){
        val intent = Intent(this, IHMParentReception::class.java)
        startActivity(intent)
        finish()
    }

    private fun IHMENFANT(){
        val intent = Intent(this, BackgroundChildrenService::class.java)
        startActivity(intent)
        finish()
    }
}