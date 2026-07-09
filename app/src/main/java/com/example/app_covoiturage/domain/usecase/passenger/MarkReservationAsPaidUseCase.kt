package com.example.app_covoiturage.domain.usecase.passenger

import com.example.app_covoiturage.domain.repository.ReservationRepository
import javax.inject.Inject

class MarkReservationAsPaidUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(reservationId: String): Result<Unit> {
        return repository.markAsPaid(reservationId)
    }
}