package com.example.perdpaslenor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class Authentification : AppCompatActivity() {
    private var boutonconf: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentification)

        boutonconf = findViewById<View>(R.id.buttonconfirmation) as Button?
        boutonconf!!.setOnClickListener { Connexion() }

        //Regarde si la permission de la localisation est accordée
        if (checkLocationPermission()) {
            val intent = Intent(this, BackgroundChildrenService::class.java)
            startService(intent)
        } else {
            // Demande la localisation si elle n'est pas active
            requestLocationPermission()
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
}