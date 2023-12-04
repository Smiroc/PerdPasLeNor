package com.example.perdpaslenor;

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging


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
            val button = findViewById(R.id.button) as Button
            button.setOnClickListener {
                Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            }
        }
        // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
//            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
    }
}
