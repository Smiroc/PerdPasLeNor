package com.example.perdpaslenor

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.Locale

class IHMParentReception : AppCompatActivity(), OnMapReadyCallback {

    private var longitude: String = "0"
    private var latitude: String = "0"
    private var etat: String = "L'enfant n'a pas lancé ou relancé l'application. Veuillez relancer l'application quand l'enfant aura initialisé la géolocalisation."
    private var dateTrack: String = "Aucune donnée disponible."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ihm_parent_reception)

        val intent = intent
        val phoneNumber = intent.getStringExtra("phoneNumber")

        val textStatus = findViewById<View>(R.id.textStatus) as TextView

        if(phoneNumber != null){
            val phoneCrypted = Authentification().encryptMD5(phoneNumber)

            val firebaseMessaging = FirebaseMessaging.getInstance()
            firebaseMessaging.token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Update the child's parent in the database
                val db = FirebaseFirestore.getInstance()
                val documentSnapshot = db.collection("user").whereEqualTo("numeroParent", phoneCrypted)

                // Get the document data
                val documentData = documentSnapshot.get().addOnSuccessListener { documents ->
                    val format = SimpleDateFormat("dd MMMM yyyy", Locale("fr"))
                    Log.w(TAG, "documents : " + documents)
                    for (document in documents) {

                        this.latitude = document.get("latitude").toString()
                        this.longitude = document.get("longitude").toString()
                        this.etat = document.get("etat").toString()
                        val dateTimeStapFor = document.get("date") as Timestamp

                        this.dateTrack = format.format(dateTimeStapFor.toDate()).toString()

                    }
                    val map: MapView = findViewById(R.id.tonEnfantEstIci)
                    map.onCreate(savedInstanceState)

                    map.getMapAsync(this)

                    textStatus.text = "Etat : " + this.etat + "\n\nDate de la dernière mise à jour : " + this.dateTrack;
                }
                .addOnFailureListener() {
                    textStatus.text = "Erreur lors de la récupération des données"
                }
            }
        }
        else{
            textStatus.text = "Erreur lors de la récupération du numéro de téléphone"
        }
    }


    override fun onMapReady(googleMap: GoogleMap){
        val endroit = LatLng(latitude.toDouble(), longitude.toDouble())
        googleMap.addMarker(MarkerOptions().position(endroit).title("Votre enfant est ici"))
        //val bounds = LatLngBounds.builder().include(endroit).build()
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(endroit, 17f)
        googleMap.moveCamera(cameraUpdate)
    }
}
