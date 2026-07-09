package com.example.app_covoiturage.domain.usecase.driver

import com.example.app_covoiturage.domain.model.Trip
import com.example.app_covoiturage.domain.repository.TripRepository
import javax.inject.Inject

class GetDriverTripsUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(): Result<List<Trip>> {
        return repository.getDriverTrips()
    }
}