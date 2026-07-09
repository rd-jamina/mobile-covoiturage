package com.example.app_covoiturage.presentation.driver.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.Vehicle
import com.example.app_covoiturage.domain.usecase.driver.GetVehiclesUseCase
import com.example.app_covoiturage.domain.usecase.driver.SaveVehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VehicleFormUiState(
    val vehicleId: String? = null,
    val brand: String = "",
    val model: String = "",
    val plateNumber: String = "",
    val seats: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class VehicleFormViewModel @Inject constructor(
    private val getVehiclesUseCase: GetVehiclesUseCase,
    private val saveVehicleUseCase: SaveVehicleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleFormUiState())
    val uiState: StateFlow<VehicleFormUiState> = _uiState

    fun loadForEdit(vehicleId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, vehicleId = vehicleId)
            val vehicles = getVehiclesUseCase().getOrDefault(emptyList())
            val vehicle = vehicles.find { it.id == vehicleId }
            if (vehicle != null) {
                _uiState.value = _uiState.value.copy(
                    brand = vehicle.brand,
                    model = vehicle.model,
                    plateNumber = vehicle.plateNumber,
                    seats = vehicle.seats.toString(),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Véhicule introuvable")
            }
        }
    }

    fun onBrandChange(v: String) { _uiState.value = _uiState.value.copy(brand = v, error = null) }
    fun onModelChange(v: String) { _uiState.value = _uiState.value.copy(model = v, error = null) }
    fun onPlateChange(v: String) { _uiState.value = _uiState.value.copy(plateNumber = v, error = null) }
    fun onSeatsChange(v: String) { _uiState.value = _uiState.value.copy(seats = v, error = null) }

    fun save() {
        val state = _uiState.value
        val seatsInt = state.seats.toIntOrNull() ?: 0
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            val result = saveVehicleUseCase(
                vehicleId = state.vehicleId,
                vehicle = Vehicle(brand = state.brand, model = state.model, plateNumber = state.plateNumber, seats = seatsInt)
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}