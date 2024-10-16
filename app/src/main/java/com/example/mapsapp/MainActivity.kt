package com.example.mapsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private lateinit var boton:Button;
    private lateinit var boton2:Button;
    private lateinit var boton3:Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        boton = findViewById(R.id.button);
        boton2 = findViewById(R.id.button2);
        boton3 = findViewById(R.id.button3)

        boton.setOnClickListener(){onClick()}
        boton2.setOnClickListener(){onClick2()}
        boton3.setOnClickListener(){onClick3()}




    }

    private fun onClick() {
        val intent: Intent?
        intent = Intent(this, MapsActivity :: class.java)
        startActivity(intent)
    }

    private fun onClick2() {
        val intent: Intent?
        intent = Intent(this, Rutas :: class.java)
        startActivity(intent)
    }

    private fun onClick3() {
        val intent: Intent?
        intent = Intent(this, SelectPointsRouteActivity :: class.java)
        startActivity(intent)
    }
}