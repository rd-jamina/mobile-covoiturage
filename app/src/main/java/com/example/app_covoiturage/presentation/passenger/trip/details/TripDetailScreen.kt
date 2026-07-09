package com.example.app_covoiturage.presentation.passenger.trip.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app_covoiturage.presentation.map.TripMapScreen

@Composable
fun TripDetailScreen(
    onBack: () -> Unit,
    onBooked: (reservationId: String, totalPrice: Double) -> Unit,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isBooked) {
        if (state.isBooked && state.bookedReservationId != null) {
            val totalPrice = (state.trip?.price ?: 0.0) * state.seats
            onBooked(state.bookedReservationId!!, totalPrice)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Info détaillé", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.trip != null) {
            val trip = state.trip!!

            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                TripMapScreen(
                    originLat = trip.originLat,
                    originLng = trip.originLng,
                    destinationLat = trip.destinationLat,
                    destinationLng = trip.destinationLng
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            DetailRow("Départ", trip.origin)
            DetailRow("Destination", trip.destination)
            DetailRow("Date/heure", trip.departureTime)
            DetailRow("Places disponibles", trip.availableSeats.toString())
            DetailRow("Prix par place", "${trip.price.toInt()} Ar")

            Spacer(modifier = Modifier.height(24.dp))
            Text("Nombre de places à réserver", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.onSeatsChange(state.seats - 1) }) {
                    Icon(Icons.Filled.Remove, contentDescription = "Diminuer")
                }
                Text(state.seats.toString(), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(horizontal = 16.dp))
                IconButton(onClick = { viewModel.onSeatsChange(state.seats + 1) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Augmenter")
                }
            }

            state.error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.book() },
                enabled = !state.isBooking,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (state.isBooking) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Réserver", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Text(state.error ?: "Trajet introuvable")
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