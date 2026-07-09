package com.example.app_covoiturage.presentation.driver.reservations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.model.Reservation
import com.example.app_covoiturage.domain.usecase.driver.GetDriverReservationsUseCase
import com.example.app_covoiturage.domain.usecase.driver.RespondToReservationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DriverReservationsUiState(
    val reservations: List<Reservation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val processingId: String? = null
)

@HiltViewModel
class DriverReservationsViewModel @Inject constructor(
    private val getDriverReservationsUseCase: GetDriverReservationsUseCase,
    private val respondToReservationUseCase: RespondToReservationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverReservationsUiState())
    val uiState: StateFlow<DriverReservationsUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = getDriverReservationsUseCase()
            result.onSuccess { list ->
                _uiState.value = DriverReservationsUiState(reservations = list, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun respond(reservationId: String, accept: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(processingId = reservationId)
            val result = respondToReservationUseCase(reservationId, accept)
            result.onSuccess {
                load()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(processingId = null, error = e.message)
            }
        }
    }
}

@Composable
fun DriverReservationsScreen(
    onReservationClick: (String) -> Unit,
    viewModel: DriverReservationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Réservations reçues", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Text(state.error ?: "Erreur", color = MaterialTheme.colorScheme.error)
            }
            state.reservations.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Aucune réservation pour le moment")
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.reservations) { reservation ->
                        ReservationCard(
                            reservation = reservation,
                            isProcessing = state.processingId == reservation.id,
                            onAccept = { viewModel.respond(reservation.id, accept = true) },
                            onReject = { viewModel.respond(reservation.id, accept = false) },
                            onClick = { onReservationClick(reservation.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationCard(
    reservation: Reservation,
    isProcessing: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(reservation.passengerName, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${reservation.origin} → ${reservation.destination}")
            Text("Départ : ${reservation.departureTime}")
            Text("Places demandées : ${reservation.seatsBooked}")
            Spacer(modifier = Modifier.height(8.dp))

            if (reservation.status == "PENDING") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAccept, enabled = !isProcessing, modifier = Modifier.weight(1f)) {
                        Text("Accepter")
                    }
                    OutlinedButton(onClick = onReject, enabled = !isProcessing, modifier = Modifier.weight(1f)) {
                        Text("Refuser")
                    }
                }
            } else {
                val (label, color) = when (reservation.status) {
                    "ACCEPTED" -> "Acceptée" to MaterialTheme.colorScheme.primary
                    "REJECTED" -> "Refusée" to MaterialTheme.colorScheme.error
                    else -> reservation.status to MaterialTheme.colorScheme.outline
                }
                Surface(color = color.copy(alpha = 0.15f), contentColor = color, shape = MaterialTheme.shapes.small) {
                    Text(label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}