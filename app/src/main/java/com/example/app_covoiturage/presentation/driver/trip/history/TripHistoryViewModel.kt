package com.example.app_covoiturage.presentation.driver.trip.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.Trip
import com.example.app_covoiturage.domain.usecase.driver.GetDriverTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripHistoryUiState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class TripHistoryViewModel @Inject constructor(
    private val getDriverTripsUseCase: GetDriverTripsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripHistoryUiState())
    val uiState: StateFlow<TripHistoryUiState> = _uiState

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getDriverTripsUseCase()
            result.onSuccess { trips ->
                _uiState.value = TripHistoryUiState(trips = trips, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}