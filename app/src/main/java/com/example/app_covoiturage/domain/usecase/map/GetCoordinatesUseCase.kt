package com.example.app_covoiturage.domain.usecase.map

import com.example.app_covoiturage.domain.repository.GeocodingRepository
import javax.inject.Inject

class GetCoordinatesUseCase @Inject constructor(
    private val repository: GeocodingRepository
) {
    suspend operator fun invoke(placeName: String): Result<Pair<Double, Double>> {
        return repository.getCoordinates(placeName)
    }
}