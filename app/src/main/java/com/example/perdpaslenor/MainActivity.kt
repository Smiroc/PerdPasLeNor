package com.example.perdpaslenor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle

import android.view.View
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private var boutonParent : Button? = null
private var boutonEnfant : Button? = null

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        boutonParent = findViewById<View>(R.id.buttonParent) as Button?
        boutonParent!!.setOnClickListener { Connexion() }

        boutonEnfant = findViewById<View>(R.id.buttonEnfant) as Button?
        boutonEnfant!!.setOnClickListener { Connexion() }
    }

    private fun Connexion() {
        val intent = Intent(this, Connexion::class.java)
        startActivity(intent)
        finish()
    }
}