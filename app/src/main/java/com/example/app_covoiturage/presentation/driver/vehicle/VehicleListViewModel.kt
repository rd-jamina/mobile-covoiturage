package com.example.app_covoiturage.presentation.driver.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.Vehicle
import com.example.app_covoiturage.domain.usecase.driver.GetVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VehicleListUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val getVehiclesUseCase: GetVehiclesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleListUiState())
    val uiState: StateFlow<VehicleListUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = getVehiclesUseCase()
            _uiState.value = VehicleListUiState(
                vehicles = result.getOrDefault(emptyList()),
                isLoading = false
            )
        }
    }
}