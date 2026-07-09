package com.example.app_covoiturage.domain.usecase.passenger

import com.example.app_covoiturage.domain.model.Trip
import com.example.app_covoiturage.domain.repository.TripRepository
import javax.inject.Inject

class GetTripDetailUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(tripId: String): Result<Trip> = repository.getTripById(tripId)
}