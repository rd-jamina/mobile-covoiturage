package com.example.app_covoiturage.domain.repository

import com.example.app_covoiturage.domain.model.Reservation

interface ReservationRepository {
    suspend fun getDriverReservations(): Result<List<Reservation>>
    suspend fun getPassengerReservations(): Result<List<Reservation>>   // ← ajouté
    suspend fun updateReservationStatus(reservationId: String, status: String): Result<Unit>
    suspend fun bookTrip(tripId: String, seats: Int): Result<String>
    suspend fun markAsPaid(reservationId: String): Result<Unit>
}