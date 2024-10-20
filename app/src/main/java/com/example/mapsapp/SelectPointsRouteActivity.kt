package com.example.mapsapp

import android.os.Bundle
import android.util.Log
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
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class SelectPointsRouteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val selectedPoints = mutableListOf<LatLng>()
    private lateinit var drawRouteButton: Button
    private val directionsApiKey = "AIzaSyDLW2C3nstWbjN7yqtTAY0Tkw8obuNQCe8"



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
            // Agregar un marcador en el punto seleccionado
            mMap.addMarker(MarkerOptions().position(latLng))

            // Imprimir las coordenadas del punto en el Logcat
            Log.d("SelectPointsRouteActivity", "Punto seleccionado: Latitud: ${latLng.latitude}, Longitud: ${latLng.longitude}")

            // Limitar a solo dos puntos
            if (selectedPoints.size == 2) {
                // Si ya hay dos puntos, reemplazar el último con el nuevo punto
                selectedPoints[1] = latLng
            } else {
                // Si hay menos de dos puntos, agregar el nuevo punto
                selectedPoints.add(latLng)
            }

            // Mostrar el botón para dibujar la ruta cuando haya 2 puntos
            if (selectedPoints.size == 2) {
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
                if (selectedPoints.size == 2) {
                    // Obtener el origen y destino de la lista selectedPoints
                    val origin = selectedPoints[0]
                    val destination = selectedPoints[1]

                    // Dibujar la ruta usando las coordenadas seleccionadas
                    drawRouteWithDirections(origin, destination)
                }
            }
            .setNegativeButton("No", null)
            .show()
    }


    private fun drawRouteWithDirections(origin: LatLng, destination: LatLng) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DirectionsService::class.java)
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&key=$directionsApiKey"
        Log.e("Generated URL", url)

        service.getDirections(
            origin = "${origin.latitude},${origin.longitude}",
            destination = "${destination.latitude},${destination.longitude}",
            key = directionsApiKey
        ).enqueue(object : retrofit2.Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: retrofit2.Response<DirectionsResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("API Response", response.body().toString()) // Imprime el cuerpo de la respuesta para depuración

                    val directions = response.body()
                    if (directions != null && directions.routes.isNotEmpty()) {
                        val route = directions.routes[0]
                        val overviewPolyline = route.overview_polyline

                        if (overviewPolyline != null) {
                            val points = overviewPolyline.points
                            val decodedPath = PolyUtil.decode(points)
                            if (decodedPath.isNotEmpty()) {
                                mMap.addPolyline(PolylineOptions().addAll(decodedPath))
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 13f))
                            } else {
                                Log.e("SelectPointsRouteActivity", "Error: Decoding the polyline returned an empty list")
                            }
                        } else {
                            Log.e("SelectPointsRouteActivity", "Error: overviewPolyline is null")
                        }
                    } else {
                        Log.e("SelectPointsRouteActivity", "Error: No routes found in the response or directions is null")
                    }
                } else {
                    Log.e("SelectPointsRouteActivity", "Error: API response unsuccessful with status ${response.code()}: ${response.message()}")
                }
            }



            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Log.e("SelectPointsRouteActivity", "Error: ${t.message}")
            }
        })
    }




    // Interfaz Retrofit para la API de Google Directions
    interface DirectionsService {
        @GET("directions/json")
        fun getDirections(
            @Query("origin") origin: String,          // Origen enviado como parámetro
            @Query("destination") destination: String, // Destino enviado como parámetro
            @Query("key") key: String,                // API Key enviada como parámetro
            @Query("mode") mode: String = "driving"   // Modo de transporte, por defecto "driving"
        ): Call<DirectionsResponse>
    }

}

// Clases para procesar la respuesta de la API de Directions
data class DirectionsResponse(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
)

data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>,
    val overview_polyline: OverviewPolyline,
    val summary: String,
    val warnings: List<String>,
    val waypoint_order: List<Any>
)

data class Bounds(
    val northeast: Location,
    val southwest: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val end_address: String,
    val end_location: Location,
    val start_address: String,
    val start_location: Location,
    val steps: List<Step>,
    val traffic_speed_entry: List<Any>,
    val via_waypoint: List<Any>
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: Location,
    val html_instructions: String,
    val maneuver: String?,
    val polyline: Polyline,
    val start_location: Location,
    val travel_mode: String
)

data class Polyline(
    val points: String
)

data class OverviewPolyline(
    val points: String
)









