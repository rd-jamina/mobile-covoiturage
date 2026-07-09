package com.example.app_covoiturage.presentation.passenger.history

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
import com.example.app_covoiturage.domain.model.Reservation

@Composable
fun PassengerHistoryScreen(
    viewModel: PassengerHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Mes réservations", style = MaterialTheme.typography.headlineSmall)
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
                        ReservationHistoryCard(reservation)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationHistoryCard(reservation: Reservation) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${reservation.origin} → ${reservation.destination}", fontWeight = FontWeight.Bold)
                StatusBadge(reservation.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Départ : ${reservation.departureTime}")
            Text("Places réservées : ${reservation.seatsBooked}")
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (label, color) = when (status) {
        "PENDING" -> "En attente" to MaterialTheme.colorScheme.tertiary
        "ACCEPTED" -> "Acceptée" to MaterialTheme.colorScheme.primary
        "REJECTED" -> "Refusée" to MaterialTheme.colorScheme.error
        "CANCELLED" -> "Annulée" to MaterialTheme.colorScheme.outline
        "COMPLETED" -> "Terminée" to MaterialTheme.colorScheme.outline
        else -> status to MaterialTheme.colorScheme.outline
    }
    Surface(color = color.copy(alpha = 0.15f), contentColor = color, shape = MaterialTheme.shapes.small) {
        Text(label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
    }
}