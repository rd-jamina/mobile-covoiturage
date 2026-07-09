package com.example.app_covoiturage.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.Role
import com.example.app_covoiturage.domain.model.User
import com.example.app_covoiturage.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedRole: Role? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val user: User? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onFullNameChange(value: String) { _uiState.value = _uiState.value.copy(fullName = value, error = null) }
    fun onEmailChange(value: String) { _uiState.value = _uiState.value.copy(email = value, error = null) }
    fun onPasswordChange(value: String) { _uiState.value = _uiState.value.copy(password = value, error = null) }
    fun onConfirmPasswordChange(value: String) { _uiState.value = _uiState.value.copy(confirmPassword = value, error = null) }
    fun onRoleSelected(role: Role) { _uiState.value = _uiState.value.copy(selectedRole = role, error = null) }

    fun register() {
        val state = _uiState.value

        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(error = "Les mots de passe saisis ne sont pas identiques")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            val result = registerUseCase(state.email, state.password, state.fullName, state.selectedRole)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true, user = user)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}