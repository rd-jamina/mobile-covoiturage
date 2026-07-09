package com.example.app_covoiturage.data.repository

import com.example.app_covoiturage.domain.model.Vehicle
import com.example.app_covoiturage.domain.repository.VehicleRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class VehicleDto(
    val id: String,
    val brand: String,
    val model: String,
    val plate_number: String,
    val seats: Int
)

@Serializable
data class VehicleInsertDto(
    val owner_id: String,
    val brand: String,
    val model: String,
    val plate_number: String,
    val seats: Int
)

class VehicleRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : VehicleRepository {

    override suspend fun getVehicles(): Result<List<Vehicle>> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Utilisateur non connecté"))

            val list = supabase.postgrest["vehicles"]
                .select { filter { eq("owner_id", userId) } }
                .decodeList<VehicleDto>()

            Result.success(list.map {
                Vehicle(id = it.id, brand = it.brand, model = it.model, plateNumber = it.plate_number, seats = it.seats)
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addVehicle(vehicle: Vehicle): Result<Unit> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Utilisateur non connecté"))

            supabase.postgrest["vehicles"].insert(
                VehicleInsertDto(
                    owner_id = userId,
                    brand = vehicle.brand,
                    model = vehicle.model,
                    plate_number = vehicle.plateNumber,
                    seats = vehicle.seats
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateVehicle(vehicleId: String, vehicle: Vehicle): Result<Unit> {
        return try {
            supabase.postgrest["vehicles"]
                .update({
                    set("brand", vehicle.brand)
                    set("model", vehicle.model)
                    set("plate_number", vehicle.plateNumber)
                    set("seats", vehicle.seats)
                }) {
                    filter { eq("id", vehicleId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteVehicle(vehicleId: String): Result<Unit> {
        return try {
            supabase.postgrest["vehicles"].delete { filter { eq("id", vehicleId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}