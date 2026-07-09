package com.example.app_covoiturage.domain.model

data class Trip(
    val id: String? = null,
    val driverId: String,
    val vehicleId: String,
    val origin: String,
    val originLat: Double = 0.0,
    val originLng: Double = 0.0,
    val destination: String,
    val destinationLat: Double = 0.0,
    val destinationLng: Double = 0.0,
    val departureTime: String,
    val availableSeats: Int,
    val price: Double,
    val status: String = "ACTIVE"
)