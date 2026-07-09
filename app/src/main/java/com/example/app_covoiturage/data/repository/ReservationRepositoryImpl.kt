package com.example.app_covoiturage.data.repository

import com.example.app_covoiturage.domain.model.Reservation
import com.example.app_covoiturage.domain.repository.ReservationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable
import javax.inject.Inject
import io.github.jan.supabase.postgrest.query.Order

@Serializable
data class TripSimpleDto(
    val origin: String,
    val destination: String,
    val departure_time: String,
    val price: Double
)

@Serializable
data class PassengerReservationJoinDto(
    val id: String,
    val trip_id: String,
    val seats_booked: Int,
    val status: String,
    val payment_status: String,
    val trips: TripSimpleDto
)

@Serializable
data class TripJoinDto(
    val origin: String,
    val destination: String,
    val departure_time: String,
    val driver_id: String
)

@Serializable
data class ProfileJoinDto(
    val full_name: String? = null
)

@Serializable
data class ReservationJoinDto(
    val id: String,
    val trip_id: String,
    val seats_booked: Int,
    val status: String,
    val trips: TripJoinDto,
    val profiles: ProfileJoinDto
)

@Serializable
data class ReservationInsertDto(
    val trip_id: String,
    val passenger_id: String,
    val seats_booked: Int
)

@Serializable
data class ReservationIdDto(val id: String)

class ReservationRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : ReservationRepository {

    override suspend fun getDriverReservations(): Result<List<Reservation>> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Utilisateur non connecté"))

            // Jointure : reservations -> trips (filtré par driver_id) + profiles (passager)
            val result = supabase.postgrest["reservations"]
                .select(
                    columns = Columns.raw(
                        "id, trip_id, seats_booked, status, trips!inner(origin, destination, departure_time, driver_id), profiles!reservations_passenger_id_fkey(full_name)"
                    )
                ) {
                    filter { eq("trips.driver_id", userId) }
                }
                .decodeList<ReservationJoinDto>()

            val reservations = result.map {
                Reservation(
                    id = it.id,
                    tripId = it.trip_id,
                    passengerName = it.profiles.full_name ?: "Passager",
                    origin = it.trips.origin,
                    destination = it.trips.destination,
                    departureTime = it.trips.departure_time,
                    seatsBooked = it.seats_booked,
                    status = it.status
                )
            }
            Result.success(reservations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReservationStatus(reservationId: String, status: String): Result<Unit> {
        return try {
            supabase.postgrest["reservations"]
                .update({ set("status", status) }) {
                    filter { eq("id", reservationId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun bookTrip(tripId: String, seats: Int): Result<String> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Utilisateur non connecté"))

            val inserted = supabase.postgrest["reservations"]
                .insert(
                    ReservationInsertDto(
                        trip_id = tripId,
                        passenger_id = userId,
                        seats_booked = seats
                    )
                ) {
                    select()
                }
                .decodeSingle<ReservationIdDto>()

            Result.success(inserted.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsPaid(reservationId: String): Result<Unit> {
        return try {
            supabase.postgrest["reservations"]
                .update({ set("payment_status", "PAID") }) {
                    filter { eq("id", reservationId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPassengerReservations(): Result<List<Reservation>> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Utilisateur non connecté"))

            val result = supabase.postgrest["reservations"]
                .select(
                    columns = Columns.raw(
                        "id, trip_id, seats_booked, status, payment_status, trips!inner(origin, destination, departure_time, price)"
                    )
                ) {
                    filter { eq("passenger_id", userId) }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<PassengerReservationJoinDto>()

            val reservations = result.map {
                Reservation(
                    id = it.id,
                    tripId = it.trip_id,
                    passengerName = "",
                    origin = it.trips.origin,
                    destination = it.trips.destination,
                    departureTime = it.trips.departure_time,
                    seatsBooked = it.seats_booked,
                    status = it.status
                )
            }
            Result.success(reservations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}