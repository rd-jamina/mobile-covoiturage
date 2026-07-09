package com.example.app_covoiturage.presentation.passenger.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.User
import com.example.app_covoiturage.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PassengerDashboardUiState(
    val user: User? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class PassengerDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PassengerDashboardUiState())
    val uiState: StateFlow<PassengerDashboardUiState> = _uiState

    init {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _uiState.value = PassengerDashboardUiState(user = user, isLoading = false)
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onDone()
        }
    }
}