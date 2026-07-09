package com.example.app_covoiturage.domain.usecase.driver

import com.example.app_covoiturage.domain.model.Reservation
import com.example.app_covoiturage.domain.repository.ReservationRepository
import javax.inject.Inject

class GetDriverReservationsUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(): Result<List<Reservation>> = repository.getDriverReservations()
}