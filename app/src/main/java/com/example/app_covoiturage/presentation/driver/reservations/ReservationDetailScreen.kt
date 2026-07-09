package com.example.app_covoiturage.presentation.driver.reservations

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app_covoiturage.domain.model.Reservation

@Composable
fun ReservationDetailScreen(
    reservationId: String,
    onBack: () -> Unit,
    viewModel: DriverReservationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val reservation = state.reservations.find { it.id == reservationId }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Détails de la demande", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        if (reservation == null) {
            Text("Réservation introuvable")
        } else {
            DetailRow("Passager", reservation.passengerName)
            DetailRow("Départ", reservation.origin)
            DetailRow("Destination", reservation.destination)
            DetailRow("Date/heure", reservation.departureTime)
            DetailRow("Places demandées", reservation.seatsBooked.toString())
            DetailRow("Statut", reservation.status)

            Spacer(modifier = Modifier.height(24.dp))

            if (reservation.status == "PENDING") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            viewModel.respond(reservation.id, accept = true)
                            onBack()
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Accepter") }
                    OutlinedButton(
                        onClick = {
                            viewModel.respond(reservation.id, accept = false)
                            onBack()
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Refuser") }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider()
}