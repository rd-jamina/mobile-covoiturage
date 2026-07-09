package com.example.app_covoiturage.presentation.passenger.profile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PassengerProfileUiState(
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val isLoading: Boolean = true,
    val isUploadingPhoto: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PassengerProfileViewModel @Inject constructor(
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PassengerProfileUiState())
    val uiState: StateFlow<PassengerProfileUiState> = _uiState

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val user = authRepository.getCurrentUser()
                val email = supabase.auth.currentUserOrNull()?.email ?: ""

                _uiState.value = PassengerProfileUiState(
                    fullName = user?.fullName ?: "",
                    phone = user?.phone ?: "",
                    email = email,
                    photoUrl = user?.photoUrl,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    // Réinsertion / remplacement de la photo de profil, directement depuis cet écran
    fun uploadPhoto(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingPhoto = true, error = null)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")

                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw Exception("Impossible de lire l'image")

                val path = "$userId/avatar.jpg"

                // upsert = true permet de REMPLACER une photo déjà existante
                supabase.storage.from("avatars").upload(path, bytes) { upsert = true }

                // On ajoute un paramètre unique pour forcer le rafraîchissement du cache d'image (Coil)
                val publicUrl = supabase.storage.from("avatars").publicUrl(path) + "?t=${System.currentTimeMillis()}"

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

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onDone()
        }
    }
}