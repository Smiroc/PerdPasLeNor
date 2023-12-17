package com.example.perdpaslenor

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import androidx.core.content.ContextCompat

class BackgroundChildrenService : Service() {
    private lateinit var executorService: ScheduledExecutorService
    private lateinit var locationManager: LocationManager

    override fun onCreate() {
        super.onCreate()

        // Créer l'exécuteur, qui va permettre de programmer des tâches
        executorService = Executors.newSingleThreadScheduledExecutor()

        // Créer le gestionnaire de localisation
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Programme la tache suivante toutes les 5 minutes
        executorService.scheduleAtFixedRate(
            Runnable {
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val fineLocationPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val coarseLocationPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val internetPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_GRANTED

                if(internetPermission) {

                    // Établis la connexion avec la base de données
                    val db = FirebaseFirestore.getInstance()
                    val documentReference = db.collection("track").document("UalHf4Je6FJOPKlB4SKv")

                    // Regarde si l'application possède la permission de la localisation
                    if (fineLocationPermission && coarseLocationPermission) {

                        // Obtiens la localisation grâce à internet
                        val location =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            val latitude = location.latitude.toString()
                            val longitude = location.longitude.toString()

                            // Met à jour la localisation de l'enfant dans la base de données
                            documentReference.update("latitude", latitude)
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error updating location: $e")
                                }
                            documentReference.update("longitude", longitude)
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error updating location: $e")
                                }
                        }
                    } else {
                        //Pas d'accès à la localisation
                        documentReference.update(
                            "etat",
                            "L'enfant n'a pas activé le positionnement de l'appareil."
                        ).addOnFailureListener { e ->
                            Log.e("Firestore", "Error updating location: $e")
                        }
                    }
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
        // Stop le service quand l'application est fermée
        //executorService.shutdownNow()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
