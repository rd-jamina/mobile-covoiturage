package com.example.app_covoiturage.domain.model

data class Reservation(
    val id: String,
    val tripId: String,
    val passengerName: String,
    val origin: String,
    val destination: String,
    val departureTime: String,
    val seatsBooked: Int,
    val status: String
)