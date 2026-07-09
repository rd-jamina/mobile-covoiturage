package com.example.app_covoiturage.presentation.passenger.trip.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.Trip
import com.example.app_covoiturage.domain.usecase.passenger.SearchTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResultsUiState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val searchTripsUseCase: SearchTripsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchResultsUiState())
    val uiState: StateFlow<SearchResultsUiState> = _uiState

    init {
        val origin = savedStateHandle.get<String>("origin") ?: ""
        val destination = savedStateHandle.get<String>("destination") ?: ""
        val date = savedStateHandle.get<String>("date") ?: ""
        search(origin, destination, date)
    }

    private fun search(origin: String, destination: String, date: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = searchTripsUseCase(origin, destination, date)
            result.onSuccess { trips ->
                _uiState.value = SearchResultsUiState(trips = trips, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}