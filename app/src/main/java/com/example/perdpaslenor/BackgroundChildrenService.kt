package com.example.perdpaslenor

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class BackgroundChildrenService : Service() {
    private lateinit var executorService: ScheduledExecutorService
    private lateinit var locationManager: LocationManager

    override fun onCreate() {
        super.onCreate()

        // Create the executor service
        executorService = Executors.newSingleThreadScheduledExecutor()

        // Create the location manager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start requesting location updates
        val handler = Handler(Looper.getMainLooper())

        executorService.scheduleAtFixedRate(
            Runnable {
                // Request location updates
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val fineLocationPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val coarseLocationPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                // Check if the app has the necessary location permission
                if (fineLocationPermission && coarseLocationPermission) {
                    Log.d("gps", "GPS")


                        // Get the current location
                        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            // Show the latitude
                        if (location != null) {

                            // Update in Firestore without fetching the document first
                            val db = FirebaseFirestore.getInstance()
                            val documentReference = db.collection("track").document("UalHf4Je6FJOPKlB4SKv")
                            val latitude = location.latitude.toString()
                            val longitude = location.longitude.toString()

                            documentReference.update("latitude", latitude).addOnFailureListener { e ->
                                Log.e("Firestore", "Error updating location: $e")
                            }
                            documentReference.update("longitude", longitude).addOnFailureListener { e ->
                                Log.e("Firestore", "Error updating location: $e")
                            }
                        } else {
                            //Gérer si l'utilisateur n'a pas de co et autre
                        }
                } else {
                    Log.d("gps", "GPS DENIED $fineLocationPermission $coarseLocationPermission")
                }
            },
            0,
            5,
            TimeUnit.MINUTES
        )


        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        // Stop location updates when the service is destroyed
        executorService.shutdownNow()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
