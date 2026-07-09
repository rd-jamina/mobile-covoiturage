package com.example.app_covoiturage.domain.usecase.driver

import com.example.app_covoiturage.domain.model.Vehicle
import com.example.app_covoiturage.domain.repository.VehicleRepository
import javax.inject.Inject

class SaveVehicleUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(vehicleId: String?, vehicle: Vehicle): Result<Unit> {
        if (vehicle.brand.isBlank() || vehicle.model.isBlank() || vehicle.plateNumber.isBlank() || vehicle.seats <= 0) {
            return Result.failure(Exception("Merci de remplir tous les champs correctement"))
        }
        return if (vehicleId == null) {
            repository.addVehicle(vehicle)
        } else {
            repository.updateVehicle(vehicleId, vehicle)
        }
    }
}