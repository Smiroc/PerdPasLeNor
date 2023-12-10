package com.example.perdpaslenor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity



class Authentification : AppCompatActivity() {
    private var boutonconf : Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentification)

        boutonconf = findViewById<View>(R.id.buttonconfirmation) as Button?
        boutonconf!!.setOnClickListener { Connexion() }
    }
    private fun Connexion() {
        val intent = Intent(this, Activity::class.java)
        startActivity(intent)
        finish()
    }
}