package com.example.app_covoiturage.presentation.driver.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject
import android.net.Uri
import android.content.ContentResolver
import io.github.jan.supabase.storage.storage

data class DriverSetupUiState(
    val phone: String = "",
    val dateOfBirth: String = "",   // ← nouveau, format "AAAA-MM-JJ"
    val photoUrl: String? = null,
    val brand: String = "",
    val model: String = "",
    val plateNumber: String = "",
    val seats: String = "1",
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val error: String? = null,
    val stepDone: Boolean = false
)

@Serializable
data class VehicleInsert(
    val owner_id: String,
    val brand: String,
    val model: String,
    val plate_number: String,
    val seats: Int
)

@HiltViewModel
class DriverSetupViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverSetupUiState())
    val uiState: StateFlow<DriverSetupUiState> = _uiState

    fun onPhoneChange(v: String) { _uiState.value = _uiState.value.copy(phone = v, error = null) }
    fun onBrandChange(v: String) { _uiState.value = _uiState.value.copy(brand = v, error = null) }
    fun onModelChange(v: String) { _uiState.value = _uiState.value.copy(model = v, error = null) }
    fun onPlateChange(v: String) { _uiState.value = _uiState.value.copy(plateNumber = v, error = null) }
    fun onSeatsChange(v: String) { _uiState.value = _uiState.value.copy(seats = v, error = null) }
    fun onDateOfBirthChange(v: String) { _uiState.value = _uiState.value.copy(dateOfBirth = v, error = null) }

    fun savePersonalInfo(onDone: () -> Unit) {
        val state = _uiState.value
        if (state.phone.isBlank()) {
            _uiState.value = state.copy(error = "Numéro de téléphone requis")
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
                        set("date_of_birth", state.dateOfBirth)
                    }) {
                        filter { eq("id", userId) }
                    }
                _uiState.value = _uiState.value.copy(isLoading = false, stepDone = true)
                onDone()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun uploadPhoto(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingPhoto = true, error = null)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")

                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw Exception("Impossible de lire l'image")

                val path = "$userId/avatar.jpg"

                supabase.storage.from("avatars").upload(path, bytes) {
                    upsert = true
                }

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

    fun saveVehicleInfo(onDone: () -> Unit) {
        val state = _uiState.value
        val seatsInt = state.seats.toIntOrNull()
        if (state.brand.isBlank() || state.model.isBlank() || state.plateNumber.isBlank() || seatsInt == null) {
            _uiState.value = state.copy(error = "Merci de remplir tous les champs correctement")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")
                supabase.postgrest["vehicles"].insert(
                    VehicleInsert(
                        owner_id = userId,
                        brand = state.brand,
                        model = state.model,
                        plate_number = state.plateNumber,
                        seats = seatsInt
                    )
                )
                _uiState.value = _uiState.value.copy(isLoading = false, stepDone = true)
                onDone()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}