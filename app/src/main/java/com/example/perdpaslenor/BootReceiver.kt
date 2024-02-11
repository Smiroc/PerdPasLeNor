package com.example.perdpaslenor

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.ImageView
import androidx.core.content.ContextCompat


public class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val serviceIntent = Intent(context, BackgroundChildrenService::class.java)

        fun serviceStart() {
            // Lance l'application en arrière-plan si le téléphone à une version supérieure ou égale à Oreo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                ContextCompat.startForegroundService(context, serviceIntent)
            } else context.startService(serviceIntent)
        }



        // Lance le service quand le téléphone démarre
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) serviceStart() else serviceStart()
    }
}