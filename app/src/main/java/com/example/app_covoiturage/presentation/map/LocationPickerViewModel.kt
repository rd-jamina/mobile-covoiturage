package com.example.app_covoiturage.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.usecase.map.GetPlaceNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationPickerUiState(
    val placeName: String? = null,
    val isLoadingName: Boolean = false
)

@HiltViewModel
class LocationPickerViewModel @Inject constructor(
    private val getPlaceNameUseCase: GetPlaceNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationPickerUiState())
    val uiState: StateFlow<LocationPickerUiState> = _uiState

    fun loadPlaceName(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingName = true)
            val result = getPlaceNameUseCase(lat, lng)
            _uiState.value = LocationPickerUiState(
                placeName = result.getOrNull(),
                isLoadingName = false
            )
        }
    }
}