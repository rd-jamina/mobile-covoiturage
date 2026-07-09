package com.example.app_covoiturage.domain.usecase.driver

import com.example.app_covoiturage.domain.model.Trip
import com.example.app_covoiturage.domain.repository.TripRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import javax.inject.Inject

class PublishTripUseCase @Inject constructor(
    private val repository: TripRepository,
    private val supabase: SupabaseClient
) {
    suspend operator fun invoke(
        origin: String,
        originLat: Double,
        originLng: Double,
        destination: String,
        destinationLat: Double,
        destinationLng: Double,
        departureTime: String,
        seats: Int,
        price: Double
    ): Result<Unit> {
        if (origin.isBlank() || destination.isBlank()) {
            return Result.failure(Exception("Origine et destination requises"))
        }
        if (departureTime.isBlank()) {
            return Result.failure(Exception("Date/heure de départ requise"))
        }
        if (seats <= 0) {
            return Result.failure(Exception("Le nombre de places doit être supérieur à 0"))
        }
        if (price < 0) {
            return Result.failure(Exception("Le prix ne peut pas être négatif"))
        }

        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return Result.failure(Exception("Utilisateur non connecté"))

        val vehicleResult = repository.getDriverVehicleId()
        val vehicleId = vehicleResult.getOrElse { return Result.failure(it) }

        val trip = Trip(
            driverId = userId,
            vehicleId = vehicleId,
            origin = origin,
            originLat = originLat,
            originLng = originLng,
            destination = destination,
            destinationLat = destinationLat,
            destinationLng = destinationLng,
            departureTime = departureTime,
            availableSeats = seats,
            price = price
        )

        return repository.publishTrip(trip)
    }
}