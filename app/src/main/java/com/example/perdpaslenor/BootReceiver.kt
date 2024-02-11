package com.example.perdpaslenor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

public class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val serviceIntent = Intent(context, BackgroundChildrenService::class.java)

        fun serviceStart() {
            // Start the foreground service on supported versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, serviceIntent) // Use ContextCompat for consistency
            } else context.startService(serviceIntent)
        }

        // Start the service when the device boots
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) serviceStart() else serviceStart()
    }
}