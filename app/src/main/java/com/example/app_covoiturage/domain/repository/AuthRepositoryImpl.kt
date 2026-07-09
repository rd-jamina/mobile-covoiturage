package com.example.app_covoiturage.data.repository

import com.example.app_covoiturage.domain.model.Role
import com.example.app_covoiturage.domain.model.User
import com.example.app_covoiturage.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class ProfileDto(
    val id: String,
    val full_name: String? = null,
    val phone: String? = null,
    val photo_url: String? = null,
    val active_role: String? = null
)

class AuthRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : AuthRepository {

    override suspend fun register(email: String, password: String, fullName: String, role: Role): Result<User> {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = kotlinx.serialization.json.buildJsonObject {
                    put("full_name", kotlinx.serialization.json.JsonPrimitive(fullName))
                }
            }
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Échec de l'inscription"))

            // Le trigger handle_new_user a déjà créé la ligne profiles (full_name)
            // On complète immédiatement avec le rôle choisi
            supabase.postgrest["profiles"]
                .update({ set("active_role", role.name) }) {
                    filter { eq("id", userId) }
                }

            Result.success(
                User(id = userId, fullName = fullName, phone = null, photoUrl = null, activeRole = role)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Échec de la connexion"))

            val profile = supabase.postgrest["profiles"]
                .select {
                    filter { eq("id", userId) }
                }
                .decodeSingle<ProfileDto>()

            Result.success(
                User(
                    id = profile.id,
                    fullName = profile.full_name,
                    phone = profile.phone,
                    photoUrl = profile.photo_url,
                    activeRole = when (profile.active_role) {
                        "DRIVER" -> Role.DRIVER
                        "PASSENGER" -> Role.PASSENGER
                        else -> null
                    }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        supabase.auth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return null
        return try {
            val profile = supabase.postgrest["profiles"]
                .select { filter { eq("id", userId) } }
                .decodeSingle<ProfileDto>()
            User(
                id = profile.id,
                fullName = profile.full_name,
                phone = profile.phone,
                photoUrl = profile.photo_url,
                activeRole = if (profile.active_role == "DRIVER") Role.DRIVER else Role.PASSENGER
            )
        } catch (e: Exception) {
            null
        }
    }

    override fun isLoggedIn(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }

    override suspend fun updateRole(role: Role): Result<Unit> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Utilisateur non connecté"))

            supabase.postgrest["profiles"]
                .update({ set("active_role", role.name) }) {
                    filter { eq("id", userId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
