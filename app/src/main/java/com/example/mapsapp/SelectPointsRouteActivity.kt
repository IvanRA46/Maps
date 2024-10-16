package com.example.mapsapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class SelectPointsRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val selectedPoints = mutableListOf<LatLng>()
    private lateinit var drawRouteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_points_route)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        drawRouteButton = findViewById(R.id.button_draw_route)
        drawRouteButton.setOnClickListener {
            confirmAndDrawRoute()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            mMap.addMarker(MarkerOptions().position(latLng))
            selectedPoints.add(latLng)

            // Mostrar el botón para dibujar la ruta cuando haya al menos 2 puntos
            if (selectedPoints.size >= 2) {
                drawRouteButton.visibility = View.VISIBLE
            }
        }
    }

    private fun confirmAndDrawRoute() {
        // Preguntar al usuario si desea trazar la ruta
        AlertDialog.Builder(this)
            .setTitle("Dibujar Ruta")
            .setMessage("¿Quieres dibujar una ruta entre los puntos seleccionados?")
            .setPositiveButton("Sí") { _, _ ->
                drawRoute()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun drawRoute() {
        val polylineOptions = PolylineOptions().geodesic(true)
        for (point in selectedPoints) {
            polylineOptions.add(point)
        }

        mMap.addPolyline(polylineOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedPoints[0], 13f))
    }
}
