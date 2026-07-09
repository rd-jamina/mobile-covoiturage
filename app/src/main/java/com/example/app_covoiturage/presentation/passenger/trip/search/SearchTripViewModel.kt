package com.example.app_covoiturage.presentation.passenger.trip.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

data class SearchTripUiState(
    val origin: String = "",
    val destination: String = "",
    val date: String = "",       // format "AAAA-MM-JJ"
    val error: String? = null
)

@HiltViewModel
class SearchTripViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SearchTripUiState())
    val uiState: StateFlow<SearchTripUiState> = _uiState

    fun onOriginChange(v: String) { _uiState.value = _uiState.value.copy(origin = v, error = null) }
    fun onDestinationChange(v: String) { _uiState.value = _uiState.value.copy(destination = v, error = null) }
    fun onDateChange(v: String) { _uiState.value = _uiState.value.copy(date = v, error = null) }

    fun validate(): Boolean {
        val state = _uiState.value
        if (state.origin.isBlank() || state.destination.isBlank()) {
            _uiState.value = state.copy(error = "Merci de renseigner le départ et l'arrivée")
            return false
        }
        return true
    }
}