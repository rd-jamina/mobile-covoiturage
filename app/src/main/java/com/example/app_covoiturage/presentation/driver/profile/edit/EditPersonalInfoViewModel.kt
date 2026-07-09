package com.example.app_covoiturage.presentation.driver.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditPersonalInfoUiState(
    val fullName: String = "",
    val phone: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class EditPersonalInfoViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPersonalInfoUiState())
    val uiState: StateFlow<EditPersonalInfoUiState> = _uiState

    init {
        loadCurrentInfo()
    }

    private fun loadCurrentInfo() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _uiState.value = _uiState.value.copy(
                fullName = user?.fullName ?: "",
                phone = user?.phone ?: "",
                isLoading = false
            )
        }
    }

    fun onFullNameChange(v: String) { _uiState.value = _uiState.value.copy(fullName = v, error = null) }
    fun onPhoneChange(v: String) { _uiState.value = _uiState.value.copy(phone = v, error = null) }

    fun save() {
        val state = _uiState.value
        if (state.fullName.isBlank()) {
            _uiState.value = state.copy(error = "Le nom ne peut pas être vide")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")

                supabase.postgrest["profiles"]
                    .update({
                        set("full_name", state.fullName)
                        set("phone", state.phone)
                    }) {
                        filter { eq("id", userId) }
                    }

                _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}