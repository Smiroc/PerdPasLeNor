package com.example.perdpaslenor

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions


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

        //Programme la tâche suivante toutes les 5 minutes
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
                    val documentReference = db.collection("track").document("UalHf4Je6FJOPKlB4SKv") // Utiliser le numéro aléatoire de dylan
                    val document = documentReference.get()

                    // Regarde si l'application possède la permission de la localisation
                    if (fineLocationPermission && coarseLocationPermission) {
                        // Obtenir la localisation grâce à Internet
                        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                        if (location != null) {
                            val latitude = location.latitude.toString()
                            val longitude = location.longitude.toString()

                            // Utiliser set avec merge pour mettre à jour ou créer les champs
                            val data = hashMapOf(
                                "latitude" to latitude,
                                "longitude" to longitude,
                                "etat" to "Connecté.",
                                "date" to Timestamp.now()
                            )

                            documentReference.set(data, SetOptions.merge())
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error updating/creating document: $e")
                                }
                        } else {
                            // Pas de localisation disponible
                            Log.e("Location", "No location available.")
                        }
                    } else {
                        // Pas d'accès à la localisation
                        val data = hashMapOf("etat" to "L'enfant n'a pas activé le positionnement de l'appareil.")

                        documentReference.set(data, SetOptions.merge())
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error updating/creating document: $e")
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
