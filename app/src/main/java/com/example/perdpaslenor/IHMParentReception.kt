package com.example.perdpaslenor

import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.maps.MapView

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class IHMParentReception : AppCompatActivity(), OnMapReadyCallback {

    var longitude: String = "0"
    var latitude: String = "0"
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ihm_parent_reception)

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        Log.w(TAG, "Fetching FCM registration token failed", )

        if(acct != null){
            val email = acct.email.toString()

            val firebaseMessaging = FirebaseMessaging.getInstance()
            firebaseMessaging.getToken().addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Update the child's parent in the database
                val db = FirebaseFirestore.getInstance()
                val documentSnapshot = db.collection("track").whereEqualTo("parent", email)

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
