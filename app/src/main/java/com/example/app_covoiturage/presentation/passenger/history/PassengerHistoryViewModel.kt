package com.example.app_covoiturage.presentation.passenger.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.Reservation
import com.example.app_covoiturage.domain.usecase.passenger.GetPassengerReservationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PassengerHistoryUiState(
    val reservations: List<Reservation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PassengerHistoryViewModel @Inject constructor(
    private val getPassengerReservationsUseCase: GetPassengerReservationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PassengerHistoryUiState())
    val uiState: StateFlow<PassengerHistoryUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getPassengerReservationsUseCase()
            result.onSuccess { list ->
                _uiState.value = PassengerHistoryUiState(reservations = list, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}