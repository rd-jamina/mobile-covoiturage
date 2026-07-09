package com.example.app_covoiturage.presentation.driver.trip.publish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.usecase.driver.PublishTripUseCase
import com.example.app_covoiturage.domain.usecase.map.GetCoordinatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PublishTripUiState(
    val origin: String = "",
    val destination: String = "",
    val departureTime: String = "",
    val seats: String = "",
    val price: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class PublishTripViewModel @Inject constructor(
    private val publishTripUseCase: PublishTripUseCase,
    private val getCoordinatesUseCase: GetCoordinatesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishTripUiState())
    val uiState: StateFlow<PublishTripUiState> = _uiState

    fun onOriginChange(v: String) { _uiState.value = _uiState.value.copy(origin = v, error = null) }
    fun onDestinationChange(v: String) { _uiState.value = _uiState.value.copy(destination = v, error = null) }
    fun onDepartureTimeChange(v: String) { _uiState.value = _uiState.value.copy(departureTime = v, error = null) }
    fun onSeatsChange(v: String) { _uiState.value = _uiState.value.copy(seats = v, error = null) }
    fun onPriceChange(v: String) { _uiState.value = _uiState.value.copy(price = v, error = null) }

    fun publish() {
        val state = _uiState.value
        val seatsInt = state.seats.toIntOrNull() ?: 0
        val priceDouble = state.price.toDoubleOrNull() ?: -1.0

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            // Géocodage automatique en arrière-plan : texte → coordonnées
            val originCoords = getCoordinatesUseCase(state.origin).getOrElse { Pair(0.0, 0.0) }
            val destCoords = getCoordinatesUseCase(state.destination).getOrElse { Pair(0.0, 0.0) }

            val result = publishTripUseCase(
                origin = state.origin,
                originLat = originCoords.first,
                originLng = originCoords.second,
                destination = state.destination,
                destinationLat = destCoords.first,
                destinationLng = destCoords.second,
                departureTime = state.departureTime,
                seats = seatsInt,
                price = priceDouble
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}