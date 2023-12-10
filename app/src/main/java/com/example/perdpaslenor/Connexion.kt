package com.example.perdpaslenor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity



class Connexion : AppCompatActivity() {
    private var boutongmail : Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connexion)

        boutongmail = findViewById<View>(R.id.buttongmail) as Button?
        boutongmail!!.setOnClickListener { Connexion() }
    }
    @SuppressLint("NotConstructor")
    private fun Connexion() {
        val intent = Intent(this, Connexion::class.java)
        startActivity(intent)
        finish()
    }
}