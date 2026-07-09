package com.example.app_covoiturage.domain.usecase.driver

import com.example.app_covoiturage.domain.model.Vehicle
import com.example.app_covoiturage.domain.repository.VehicleRepository
import javax.inject.Inject

class GetVehiclesUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(): Result<List<Vehicle>> = repository.getVehicles()
}