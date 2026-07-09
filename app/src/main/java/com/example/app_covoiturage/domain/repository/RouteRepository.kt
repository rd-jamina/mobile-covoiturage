package com.example.app_covoiturage.domain.repository

interface RouteRepository {
    suspend fun getRoute(originLat: Double, originLng: Double, destLat: Double, destLng: Double): Result<List<Pair<Double, Double>>>
}