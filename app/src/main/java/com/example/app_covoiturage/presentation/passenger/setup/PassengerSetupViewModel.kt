package com.example.app_covoiturage.presentation.passenger.setup

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PassengerSetupUiState(
    val phone: String = "",
    val gender: String? = null,       // "MALE" ou "FEMALE"
    val dateOfBirth: String = "",     // format "AAAA-MM-JJ"
    val photoUrl: String? = null,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class PassengerSetupViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(PassengerSetupUiState())
    val uiState: StateFlow<PassengerSetupUiState> = _uiState

    fun onPhoneChange(v: String) { _uiState.value = _uiState.value.copy(phone = v, error = null) }
    fun onGenderChange(v: String) { _uiState.value = _uiState.value.copy(gender = v, error = null) }
    fun onDateOfBirthChange(v: String) { _uiState.value = _uiState.value.copy(dateOfBirth = v, error = null) }

    fun uploadPhoto(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingPhoto = true, error = null)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")

                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw Exception("Impossible de lire l'image")

                val path = "$userId/avatar.jpg"
                supabase.storage.from("avatars").upload(path, bytes) { upsert = true }
                val publicUrl = supabase.storage.from("avatars").publicUrl(path)

                supabase.postgrest["profiles"]
                    .update({ set("photo_url", publicUrl) }) {
                        filter { eq("id", userId) }
                    }

                _uiState.value = _uiState.value.copy(isUploadingPhoto = false, photoUrl = publicUrl)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isUploadingPhoto = false, error = "Échec de l'upload : ${e.message}")
            }
        }
    }

    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        if (state.phone.isBlank()) {
            _uiState.value = state.copy(error = "Numéro de téléphone requis")
            return
        }
        if (state.gender == null) {
            _uiState.value = state.copy(error = "Merci de sélectionner un genre")
            return
        }
        if (state.dateOfBirth.isBlank()) {
            _uiState.value = state.copy(error = "Date de naissance requise")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")

                supabase.postgrest["profiles"]
                    .update({
                        set("phone", state.phone)
                        set("gender", state.gender)
                        set("date_of_birth", state.dateOfBirth)
                    }) {
                        filter { eq("id", userId) }
                    }

                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                onDone()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}