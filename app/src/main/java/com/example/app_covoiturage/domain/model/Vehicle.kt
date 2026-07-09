package com.example.app_covoiturage.domain.model

data class Vehicle(
    val id: String? = null,
    val brand: String,
    val model: String,
    val plateNumber: String,
    val seats: Int
)