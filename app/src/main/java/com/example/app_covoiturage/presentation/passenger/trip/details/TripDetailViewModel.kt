package com.example.app_covoiturage.presentation.passenger.trip.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.Trip
import com.example.app_covoiturage.domain.usecase.passenger.BookTripUseCase
import com.example.app_covoiturage.domain.usecase.passenger.GetTripDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripDetailUiState(
    val trip: Trip? = null,
    val seats: Int = 1,
    val isLoading: Boolean = true,
    val isBooking: Boolean = false,
    val error: String? = null,
    val isBooked: Boolean = false,
    val bookedReservationId: String? = null
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val getTripDetailUseCase: GetTripDetailUseCase,
    private val bookTripUseCase: BookTripUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState

    private val tripId: String = savedStateHandle.get<String>("tripId") ?: ""

    init {
        loadTrip()
    }

    private fun loadTrip() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getTripDetailUseCase(tripId)
            result.onSuccess { trip ->
                _uiState.value = _uiState.value.copy(trip = trip, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onSeatsChange(seats: Int) {
        if (seats >= 1) _uiState.value = _uiState.value.copy(seats = seats)
    }

    fun book() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBooking = true, error = null)
            val result = bookTripUseCase(tripId, _uiState.value.seats)
            result.onSuccess { reservationId ->
                _uiState.value = _uiState.value.copy(
                    isBooking = false,
                    isBooked = true,
                    bookedReservationId = reservationId
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isBooking = false, error = e.message)
            }
        }
    }
}