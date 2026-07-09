package com.example.app_covoiturage.domain.usecase.passenger

import com.example.app_covoiturage.domain.model.Trip
import com.example.app_covoiturage.domain.repository.TripRepository
import javax.inject.Inject

class SearchTripsUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(origin: String, destination: String, date: String): Result<List<Trip>> {
        return repository.searchTrips(origin, destination, date)
    }
}