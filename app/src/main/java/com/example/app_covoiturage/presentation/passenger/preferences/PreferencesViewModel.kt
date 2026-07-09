package com.example.app_covoiturage.presentation.passenger.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class PreferencesDto(
    val music_allowed: Boolean = true,
    val smoking_allowed: Boolean = false,
    val pets_allowed: Boolean = false,
    val chat_preference: String = "MODERATE"
)

@Serializable
data class PreferencesUpsertDto(
    val user_id: String,
    val music_allowed: Boolean,
    val smoking_allowed: Boolean,
    val pets_allowed: Boolean,
    val chat_preference: String
)

data class PreferencesUiState(
    val musicAllowed: Boolean = true,
    val smokingAllowed: Boolean = false,
    val petsAllowed: Boolean = false,
    val chatPreference: String = "MODERATE",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = _uiState

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
                val prefs = supabase.postgrest["travel_preferences"]
                    .select { filter { eq("user_id", userId) } }
                    .decodeSingleOrNull<PreferencesDto>()

                if (prefs != null) {
                    _uiState.value = PreferencesUiState(
                        musicAllowed = prefs.music_allowed,
                        smokingAllowed = prefs.smoking_allowed,
                        petsAllowed = prefs.pets_allowed,
                        chatPreference = prefs.chat_preference,
                        isLoading = false
                    )
                } else {
                    // Aucune préférence enregistrée encore — valeurs par défaut
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onMusicChange(v: Boolean) { _uiState.value = _uiState.value.copy(musicAllowed = v) }
    fun onSmokingChange(v: Boolean) { _uiState.value = _uiState.value.copy(smokingAllowed = v) }
    fun onPetsChange(v: Boolean) { _uiState.value = _uiState.value.copy(petsAllowed = v) }
    fun onChatPreferenceChange(v: String) { _uiState.value = _uiState.value.copy(chatPreference = v) }

    fun save() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id
                    ?: throw Exception("Utilisateur non connecté")

                // Upsert : insère si absent, met à jour si déjà existant (grâce à la contrainte unique sur user_id)
                supabase.postgrest["travel_preferences"].upsert(
                    PreferencesUpsertDto(
                        user_id = userId,
                        music_allowed = state.musicAllowed,
                        smoking_allowed = state.smokingAllowed,
                        pets_allowed = state.petsAllowed,
                        chat_preference = state.chatPreference
                    )
                ) {
                    onConflict = "user_id"
                }

                _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}