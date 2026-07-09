package com.example.app_covoiturage.data.repository

import com.example.app_covoiturage.data.remote.osrm.OsrmApiService
import com.example.app_covoiturage.domain.repository.RouteRepository
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val osrmApiService: OsrmApiService
) : RouteRepository {

    override suspend fun getRoute(
        originLat: Double, originLng: Double,
        destLat: Double, destLng: Double
    ): Result<List<Pair<Double, Double>>> {
        return try {
            val coordinates = "$originLng,$originLat;$destLng,$destLat"
            val response = osrmApiService.getRoute(coordinates)

            if (response.routes.isEmpty()) {
                return Result.failure(Exception("Aucun itinéraire trouvé"))
            }

            val points = response.routes.first().geometry.coordinates.map { coord ->
                Pair(coord[1], coord[0]) // [lat, lng]
            }
            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}