package com.example.app_covoiturage.domain.usecase.driver

import com.example.app_covoiturage.domain.repository.ReservationRepository
import javax.inject.Inject

class RespondToReservationUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(reservationId: String, accept: Boolean): Result<Unit> {
        return repository.updateReservationStatus(reservationId, if (accept) "ACCEPTED" else "REJECTED")
    }
}