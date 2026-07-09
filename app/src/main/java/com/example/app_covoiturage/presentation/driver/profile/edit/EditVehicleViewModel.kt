package com.example.app_covoiturage.presentation.driver.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
data class VehicleEditDto(
    val brand: String,
    val model: String,
    val plate_number: String,
    val seats: Int
)

data class EditVehicleUiState(
    val brand: String = "",
    val model: String = "",
    val plateNumber: String = "",
    val seats: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class EditVehicleViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditVehicleUiState())
    val uiState: StateFlow<EditVehicleUiState> = _uiState

    init {
        loadCurrentVehicle()
    }

    private fun loadCurrentVehicle() {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")

                val vehicle = supabase.postgrest["vehicles"]
                    .select { filter { eq("owner_id", userId) } }
                    .decodeSingle<VehicleEditDto>()

                _uiState.value = _uiState.value.copy(
                    brand = vehicle.brand,
                    model = vehicle.model,
                    plateNumber = vehicle.plate_number,
                    seats = vehicle.seats.toString(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Aucun véhicule trouvé")
            }
        }
    }

    fun onBrandChange(v: String) { _uiState.value = _uiState.value.copy(brand = v, error = null) }
    fun onModelChange(v: String) { _uiState.value = _uiState.value.copy(model = v, error = null) }
    fun onPlateChange(v: String) { _uiState.value = _uiState.value.copy(plateNumber = v, error = null) }
    fun onSeatsChange(v: String) { _uiState.value = _uiState.value.copy(seats = v, error = null) }

    fun save() {
        val state = _uiState.value
        val seatsInt = state.seats.toIntOrNull()
        if (state.brand.isBlank() || state.model.isBlank() || state.plateNumber.isBlank() || seatsInt == null) {
            _uiState.value = state.copy(error = "Merci de remplir tous les champs correctement")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")

                supabase.postgrest["vehicles"]
                    .update({
                        set("brand", state.brand)
                        set("model", state.model)
                        set("plate_number", state.plateNumber)
                        set("seats", seatsInt)
                    }) {
                        filter { eq("owner_id", userId) }
                    }

                _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}