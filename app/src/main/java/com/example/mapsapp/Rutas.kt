package com.example.mapsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Rutas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rutas)

        val originInput = findViewById<EditText>(R.id.originInput)
        val destinationInput = findViewById<EditText>(R.id.destinationInput)
        val btnOpenMaps = findViewById<Button>(R.id.btnOpenMaps)

        btnOpenMaps.setOnClickListener {
            val origin = originInput.text.toString()
            val destination = destinationInput.text.toString()

            if (origin.isNotEmpty() && destination.isNotEmpty()) {
                val gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }
    }
}