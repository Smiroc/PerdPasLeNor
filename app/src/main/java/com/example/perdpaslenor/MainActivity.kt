package com.example.perdpaslenor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BootReceiver().onReceive(this, Intent())

    }

    override fun onStart() {
        super.onStart()
    }
}