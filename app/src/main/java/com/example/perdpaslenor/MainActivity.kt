package com.example.perdpaslenor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkLocationPermission()) {
            val intent = Intent(this, BackgroundChildrenService::class.java)
            intent.action = "com.example.ACTION_START_BACKGROUND_SERVICE"
            startService(intent)

            //val intentMap = Intent(this, IHMParentReception::class.java)
            //startActivity(intentMap)
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
            LOCATION_PERMISSION_REQUEST_CODE
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
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Regarde si la permission est accordée
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée
                Log.d("test", "grantResult")

                val intentBgChild = Intent(this, BackgroundChildrenService::class.java)
//                val intentMap = Intent(this, IHMParentReception::class.java)

                startService(intentBgChild)
//                startActivity(intentMap)
            } else {
                // Permission refusé, re-demande la permission
                requestLocationPermission()
            }
        }
    }
}