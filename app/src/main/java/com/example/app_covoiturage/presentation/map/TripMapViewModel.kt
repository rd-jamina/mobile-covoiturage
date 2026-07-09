package com.example.app_covoiturage.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.usecase.map.GetRouteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

data class GeoPointSimple(val latitude: Double, val longitude: Double)

data class TripMapUiState(
    val routePoints: List<GeoPointSimple> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class TripMapViewModel @Inject constructor(
    private val getRouteUseCase: GetRouteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripMapUiState())
    val uiState: StateFlow<TripMapUiState> = _uiState

    fun loadRoute(originLat: Double, originLng: Double, destLat: Double, destLng: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = getRouteUseCase(originLat, originLng, destLat, destLng)
            result.onSuccess { points ->
                _uiState.value = TripMapUiState(
                    routePoints = points.map { GeoPointSimple(it.first, it.second) },
                    isLoading = false
                )
            }.onFailure {
                _uiState.value = TripMapUiState(routePoints = emptyList(), isLoading = false)
            }
        }
    }
}