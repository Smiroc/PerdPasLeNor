package com.example.perdpaslenor;

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore

class ChildActivity : AppCompatActivity() {
    lateinit var firebaseMessaging: FirebaseMessaging


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child)

        firebaseMessaging = FirebaseMessaging.getInstance()
        firebaseMessaging.getToken().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            val button = findViewById<Button>(R.id.button)
            button.setOnClickListener {
                val db = FirebaseFirestore.getInstance()
                val documentSnapshot = db.collection("track").document("UalHf4Je6FJOPKlB4SKv")
                val data = hashMapOf(
                    "child" to "Titouan",
                    "parent" to "Fabrice"
                )
                documentSnapshot.set(data)

            }
        }
    }
}
