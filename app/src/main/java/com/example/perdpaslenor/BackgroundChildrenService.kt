package com.example.perdpaslenor

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


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

                // Verifie toutes les permissions
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

                if (internetPermission) {
                    var phoneNumber = "999999"
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_PHONE_STATE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val telephonyManager =
                            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        phoneNumber =
                            telephonyManager.line1Number?.takeIf { it.isNotBlank() }.toString()
                    }

                    // Établis la connexion avec la base de données
                    val db = FirebaseFirestore.getInstance()
                    val documentSnapshot =
                        db.collection("user").whereEqualTo("numeroEnfant", phoneNumber)

                    // Regarde si l'application possède la permission de la localisation
                    if (fineLocationPermission && coarseLocationPermission) {
                        // Obtenir la localisation grâce à Internet
                        val location =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                        if (location != null) {
                            val latitude = location.latitude.toString()
                            val longitude = location.longitude.toString()

                            // Map des éléments à ajouter dans la base de données
                            val data = hashMapOf(
                                "numeroEnfant" to phoneNumber,
                                "latitude" to latitude,
                                "longitude" to longitude,
                                "etat" to "Connecté.",
                                "date" to Timestamp.now()
                            )

                            // Ajoute les éléments dans la base de données
                            documentSnapshot.get().addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val documentReference =
                                        db.collection("user").document(document.id)
                                    documentReference.set(data, SetOptions.merge())
                                        .addOnFailureListener { e ->
                                            Log.e(
                                                "Firestore",
                                                "Error updating/creating document: $e"
                                            )
                                        }
                                }
                            }
                        } else {
                            // Pas de localisation disponible
                            Log.e("Location", "No location available.")
                        }
                    } else {
                        // Pas d'accès à la localisation
                        val data =
                            hashMapOf("etat" to "L'enfant n'a pas activé le positionnement de l'appareil.")

                        documentSnapshot.get().addOnSuccessListener { documents ->
                            for (document in documents) {
                                val documentReference =
                                    db.collection("user").document(document.id)
                                documentReference.set(data, SetOptions.merge())
                                    .addOnFailureListener { e ->
                                        Log.e(
                                            "Firestore",
                                            "Error updating/creating document: $e"
                                        )
                                    }
                            }
                        }
                    }
                }
            },
            0,
            5,
            TimeUnit.SECONDS
        )
        val notificationId = 123

        fun createNotification() {
            // Créer le canal de notification pour les appareils avec Android Oreo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "456"
                val channelName = "PerdPasLeNor"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }

            // Création de la notification
            val notification = NotificationCompat.Builder(this, "456")
                .setContentTitle("")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .build()

            // Affiche la notification
            startForeground(notificationId, notification)
        }
        createNotification()

        return START_STICKY

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
