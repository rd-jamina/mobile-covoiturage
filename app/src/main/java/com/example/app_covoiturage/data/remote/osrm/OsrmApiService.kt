package com.example.app_covoiturage.data.remote.osrm

import retrofit2.http.GET
import retrofit2.http.Path

interface OsrmApiService {
    // Format OSRM : /route/v1/driving/{lng1},{lat1};{lng2},{lat2}?overview=full&geometries=geojson
    @GET("route/v1/driving/{coordinates}")
    suspend fun getRoute(
        @Path("coordinates") coordinates: String,
        @retrofit2.http.Query("overview") overview: String = "full",
        @retrofit2.http.Query("geometries") geometries: String = "geojson"
    ): OsrmResponse
}