package com.example.app_covoiturage.domain.usecase.map

import com.example.app_covoiturage.domain.repository.RouteRepository
import javax.inject.Inject

class GetRouteUseCase @Inject constructor(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(originLat: Double, originLng: Double, destLat: Double, destLng: Double): Result<List<Pair<Double, Double>>> {
        return repository.getRoute(originLat, originLng, destLat, destLng)
    }
}