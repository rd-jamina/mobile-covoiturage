package com.example.app_covoiturage.domain.repository

import com.example.app_covoiturage.domain.model.Trip

interface TripRepository {
    suspend fun publishTrip(trip: Trip): Result<Unit>
    suspend fun getDriverVehicleId(): Result<String>
    suspend fun getDriverTrips(): Result<List<Trip>>
    suspend fun searchTrips(origin: String, destination: String, date: String): Result<List<Trip>>
    suspend fun searchTripsNearby(
        originLat: Double, originLng: Double,
        destinationLat: Double, destinationLng: Double,
        date: String
    ): Result<List<Trip>>
    suspend fun getTripById(tripId: String): Result<Trip>
}