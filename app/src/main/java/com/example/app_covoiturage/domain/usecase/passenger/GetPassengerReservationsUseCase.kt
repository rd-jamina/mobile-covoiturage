package com.example.app_covoiturage.domain.usecase.passenger

import com.example.app_covoiturage.domain.model.Reservation
import com.example.app_covoiturage.domain.repository.ReservationRepository
import javax.inject.Inject

class GetPassengerReservationsUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(): Result<List<Reservation>> = repository.getPassengerReservations()
}