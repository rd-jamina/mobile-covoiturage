package com.example.app_covoiturage.domain.repository

import com.example.app_covoiturage.domain.model.Vehicle

interface VehicleRepository {
    suspend fun getVehicles(): Result<List<Vehicle>>
    suspend fun addVehicle(vehicle: Vehicle): Result<Unit>
    suspend fun updateVehicle(vehicleId: String, vehicle: Vehicle): Result<Unit>
    suspend fun deleteVehicle(vehicleId: String): Result<Unit>
}