package com.example.app_covoiturage.domain.usecase.passenger

import com.example.app_covoiturage.domain.repository.ReservationRepository
import javax.inject.Inject

class BookTripUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(tripId: String, seats: Int): Result<String> {
        if (seats <= 0) return Result.failure(Exception("Nombre de places invalide"))
        return repository.bookTrip(tripId, seats)
    }
}