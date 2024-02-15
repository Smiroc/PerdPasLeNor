package com.example.perdpaslenor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        BootReceiver().onReceive(this, Intent())

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}