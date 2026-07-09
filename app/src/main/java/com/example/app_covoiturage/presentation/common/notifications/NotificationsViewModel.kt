package com.example.app_covoiturage.presentation.common.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
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

data class NotificationsUiState(
    val notifications: List<NotificationDto> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
                val list = supabase.postgrest["notifications"]
                    .select {
                        filter { eq("user_id", userId) }
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<NotificationDto>()
                _uiState.value = NotificationsUiState(notifications = list, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                supabase.postgrest["notifications"]
                    .update({ set("is_read", true) }) {
                        filter { eq("id", notificationId) }
                    }
                load()
            } catch (e: Exception) {
                // silencieux, pas critique
            }
        }
    }
}