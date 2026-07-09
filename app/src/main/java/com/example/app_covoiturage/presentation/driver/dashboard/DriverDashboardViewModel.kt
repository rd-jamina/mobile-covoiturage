package com.example.app_covoiturage.presentation.driver.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.User
import com.example.app_covoiturage.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DriverDashboardUiState(
    val user: User? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class DriverDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverDashboardUiState())
    val uiState: StateFlow<DriverDashboardUiState> = _uiState

    init {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _uiState.value = DriverDashboardUiState(user = user, isLoading = false)
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onDone()
        }
    }
}