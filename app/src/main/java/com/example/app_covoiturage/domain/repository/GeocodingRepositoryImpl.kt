package com.example.app_covoiturage.data.repository

import com.example.app_covoiturage.data.remote.nominatim.NominatimApiService
import com.example.app_covoiturage.domain.repository.GeocodingRepository
import javax.inject.Inject

class GeocodingRepositoryImpl @Inject constructor(
    private val nominatimApiService: NominatimApiService
) : GeocodingRepository {

    override suspend fun getPlaceName(lat: Double, lng: Double): Result<String> {
        return try {
            val response = nominatimApiService.reverseGeocode(lat, lng)
            val address = response.address

            // Priorité : ville > village > état, sinon nom complet, sinon coordonnées
            val name = address?.city
                ?: address?.town
                ?: address?.village
                ?: address?.county
                ?: response.displayName?.split(",")?.firstOrNull()?.trim()
                ?: "Lieu (${"%.4f".format(lat)}, ${"%.4f".format(lng)})"

            Result.success(name)
        } catch (e: Exception) {
            // En cas d'échec réseau, on retourne quand même des coordonnées lisibles
            Result.success("Lieu (${"%.4f".format(lat)}, ${"%.4f".format(lng)})")
        }
    }
    override suspend fun getCoordinates(placeName: String): Result<Pair<Double, Double>> {
        return try {
            val results = nominatimApiService.forwardGeocode(placeName)
            if (results.isEmpty()) {
                return Result.failure(Exception("Lieu introuvable : $placeName"))
            }
            val result = results.first()
            Result.success(Pair(result.lat.toDouble(), result.lon.toDouble()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}