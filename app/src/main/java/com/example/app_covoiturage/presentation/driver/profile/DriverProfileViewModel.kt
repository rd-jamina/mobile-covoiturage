package com.example.app_covoiturage.presentation.driver.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class VehicleProfileDto(
    val brand: String,
    val model: String,
    val plate_number: String,
    val seats: Int
)

data class DriverProfileUiState(
    val fullName: String = "",
    val phone: String = "",
    val vehicle: VehicleProfileDto? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class DriverProfileViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverProfileUiState())
    val uiState: StateFlow<DriverProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val user = authRepository.getCurrentUser()
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")

                val vehicle = try {
                    supabase.postgrest["vehicles"]
                        .select { filter { eq("owner_id", userId) } }
                        .decodeSingle<VehicleProfileDto>()
                } catch (e: Exception) {
                    null // pas grave si aucun véhicule trouvé
                }

                _uiState.value = DriverProfileUiState(
                    fullName = user?.fullName ?: "",
                    phone = user?.phone ?: "",
                    vehicle = vehicle,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onDone()
        }
    }
}