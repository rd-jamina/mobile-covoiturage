package com.example.app_covoiturage.domain.repository

interface GeocodingRepository {
    suspend fun getPlaceName(lat: Double, lng: Double): Result<String>
    suspend fun getCoordinates(placeName: String): Result<Pair<Double, Double>>
}