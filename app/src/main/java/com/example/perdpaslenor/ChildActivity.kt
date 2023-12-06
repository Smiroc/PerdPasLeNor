package com.example.perdpaslenor;

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ChildActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child)

        val executorService = Executors.newSingleThreadScheduledExecutor()

        val task = Runnable {
            // Update data in Firestore
            val db = FirebaseFirestore.getInstance()
            val documentSnapshot = db.collection("track").document("UalHf4Je6FJOPKlB4SKv")
            val txt = findViewById<TextView>(R.id.editTextText)

            documentSnapshot.update("child", "${txt.text}")
        }
        executorService.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS)
    }
}