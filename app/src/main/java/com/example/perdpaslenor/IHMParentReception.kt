package com.example.perdpaslenor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.MapView

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds

class IHMParentReception : AppCompatActivity(), OnMapReadyCallback {

    val longitude = 3.291738397368126
    val latitude = 49.8455874008029

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ihm_parent_reception)
        val map: MapView = findViewById(R.id.tonEnfantEstIci)
        map.onCreate(savedInstanceState)

        map.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap){
        val endroit = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(endroit).title("Votre enfant est ici"))
        //val bounds = LatLngBounds.builder().include(endroit).build()
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(endroit, 17f)
        googleMap.moveCamera(cameraUpdate)
    }

}
