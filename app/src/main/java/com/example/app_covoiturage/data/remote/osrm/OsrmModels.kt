package com.example.app_covoiturage.data.remote.osrm

import com.google.gson.annotations.SerializedName

data class OsrmResponse(
    val routes: List<OsrmRoute>,
    val code: String
)

data class OsrmRoute(
    val geometry: OsrmGeometry,
    val distance: Double,   // en mètres
    val duration: Double    // en secondes
)

data class OsrmGeometry(
    val coordinates: List<List<Double>>   // [ [lng, lat], [lng, lat], ... ]
)