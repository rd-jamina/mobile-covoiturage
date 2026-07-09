package com.example.app_covoiturage.data.remote.nominatim

import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json",
        @Query("accept-language") language: String = "fr"
    ): NominatimResponse

    // Nouveau : texte → coordonnées
    @GET("search")
    suspend fun forwardGeocode(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1,
        @Query("countrycodes") countryCode: String = "mg"  // limite à Madagascar
    ): List<NominatimSearchResult>
}

data class NominatimSearchResult(
    val lat: String,
    val lon: String,
    val display_name: String
)