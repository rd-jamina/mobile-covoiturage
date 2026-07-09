package com.example.app_covoiturage.data.repository

import com.example.app_covoiturage.domain.model.Trip
import com.example.app_covoiturage.domain.repository.TripRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class TripInsert(
    val driver_id: String,
    val vehicle_id: String,
    val origin: String,
    val origin_lat: Double,
    val origin_lng: Double,
    val destination: String,
    val destination_lat: Double,
    val destination_lng: Double,
    val departure_time: String,
    val available_seats: Int,
    val price: Double
)

@Serializable
data class TripDto(
    val id: String,
    val driver_id: String,
    val vehicle_id: String,
    val origin: String,
    val origin_lat: Double? = null,
    val origin_lng: Double? = null,
    val destination: String,
    val destination_lat: Double? = null,
    val destination_lng: Double? = null,
    val departure_time: String,
    val available_seats: Int,
    val price: Double,
    val status: String
)

@Serializable
data class SearchTripsRpcParams(
    val p_origin_lat: Double,
    val p_origin_lng: Double,
    val p_destination_lat: Double,
    val p_destination_lng: Double,
    val p_date: String,
    val p_radius_km: Double = 15.0
)

@Serializable
data class VehicleIdDto(val id: String)

class TripRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : TripRepository {

    // Mapper commun pour éviter la duplication de code
    private fun TripDto.toDomain(): Trip {
        return Trip(
            id = id,
            driverId = driver_id,
            vehicleId = vehicle_id,
            origin = origin,
            originLat = origin_lat ?: 0.0,
            originLng = origin_lng ?: 0.0,
            destination = destination,
            destinationLat = destination_lat ?: 0.0,
            destinationLng = destination_lng ?: 0.0,
            departureTime = departure_time,
            availableSeats = available_seats,
            price = price,
            status = status
        )
    }

    override suspend fun getDriverTrips(): Result<List<Trip>> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Utilisateur non connecté"))

            val trips = supabase.postgrest["trips"]
                .select {
                    filter { eq("driver_id", userId) }
                    order("departure_time", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<TripDto>()

            Result.success(trips.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchTrips(
        origin: String,
        destination: String,
        date: String
    ): Result<List<Trip>> {
        return try {
            val trips = supabase.postgrest["trips"]
                .select {
                    filter {
                        ilike("origin", "%$origin%")
                        ilike("destination", "%$destination%")
                        gte("departure_time", date)
                    }
                    order("departure_time", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<TripDto>()

            Result.success(trips.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun publishTrip(trip: Trip): Result<Unit> {
        return try {
            supabase.postgrest["trips"].insert(
                TripInsert(
                    driver_id = trip.driverId,
                    vehicle_id = trip.vehicleId,
                    origin = trip.origin,
                    origin_lat = trip.originLat,
                    origin_lng = trip.originLng,
                    destination = trip.destination,
                    destination_lat = trip.destinationLat,
                    destination_lng = trip.destinationLng,
                    departure_time = trip.departureTime,
                    available_seats = trip.availableSeats,
                    price = trip.price
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDriverVehicleId(): Result<String> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Utilisateur non connecté"))

            val vehicle = supabase.postgrest["vehicles"]
                .select {
                    filter { eq("owner_id", userId) }
                }
                .decodeSingle<VehicleIdDto>()

            Result.success(vehicle.id)
        } catch (e: Exception) {
            Result.failure(Exception("Aucun véhicule trouvé — ajoute d'abord un véhicule"))
        }
    }



    override suspend fun searchTripsNearby(
        originLat: Double, originLng: Double,
        destinationLat: Double, destinationLng: Double,
        date: String
    ): Result<List<Trip>> {
        return try {
            val params = kotlinx.serialization.json.Json.encodeToJsonElement(
                SearchTripsRpcParams(
                    p_origin_lat = originLat,
                    p_origin_lng = originLng,
                    p_destination_lat = destinationLat,
                    p_destination_lng = destinationLng,
                    p_date = date
                )
            ).jsonObject

            val trips = supabase.postgrest.rpc("search_trips_nearby", params)
                .decodeList<TripDto>()

            Result.success(trips.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTripById(tripId: String): Result<Trip> {
        return try {
            val trip = supabase.postgrest["trips"]
                .select { filter { eq("id", tripId) } }
                .decodeSingle<TripDto>()

            Result.success(trip.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}