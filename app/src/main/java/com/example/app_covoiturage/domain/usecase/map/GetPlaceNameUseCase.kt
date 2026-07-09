package com.example.app_covoiturage.domain.usecase.map

import com.example.app_covoiturage.domain.repository.GeocodingRepository
import javax.inject.Inject

class GetPlaceNameUseCase @Inject constructor(
    private val repository: GeocodingRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double): Result<String> {
        return repository.getPlaceName(lat, lng)
    }
}