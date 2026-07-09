package com.example.app_covoiturage.presentation.driver.notifications

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
data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val is_read: Boolean,
    val created_at: String
)

data class DriverNotificationsUiState(
    val notifications: List<NotificationDto> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DriverNotificationsViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverNotificationsUiState())
    val uiState: StateFlow<DriverNotificationsUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
                val list = supabase.postgrest["notifications"]
                    .select {
                        filter { eq("user_id", userId) }
                        order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    }
                    .decodeList<NotificationDto>()
                _uiState.value = DriverNotificationsUiState(notifications = list, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}