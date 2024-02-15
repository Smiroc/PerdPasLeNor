package com.example.perdpaslenor

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class IHMParentReception : AppCompatActivity(), OnMapReadyCallback {

    private var longitude: String = "0"
    private var latitude: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ihm_parent_reception)

        val intent = intent
        val phoneNumber = intent.getStringExtra("phoneNumber")

        if(phoneNumber != null){
            val phoneCrypted = Authentification().encryptMD5(phoneNumber)

            Log.d(TAG, "onCreate: $phoneNumber")

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
                    for (document in documents) {

                        this.latitude = document.get("latitude").toString()
                        this.longitude = document.get("longitude").toString()
                    }
                    val map: MapView = findViewById(R.id.tonEnfantEstIci)
                    map.onCreate(savedInstanceState)

                    map.getMapAsync(this)
                }
            }
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
