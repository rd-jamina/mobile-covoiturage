package com.example.app_covoiturage.presentation.driver.trip.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app_covoiturage.domain.model.Trip

@Composable
fun TripHistoryScreen(
    onViewMap: (Trip) -> Unit,
    viewModel: TripHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Historique des trajets", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Text(state.error ?: "Erreur inconnue", color = MaterialTheme.colorScheme.error)
            }
            state.trips.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Aucun trajet publié pour le moment")
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.trips) { trip ->
                        TripHistoryCard(trip, onViewMap = { onViewMap(trip) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TripHistoryCard(trip: Trip, onViewMap: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${trip.origin} → ${trip.destination}", fontWeight = FontWeight.Bold)
                StatusBadge(trip.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Départ : ${trip.departureTime}")
            Text("Places : ${trip.availableSeats}  •  Prix : ${trip.price.toInt()} Ar")
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onViewMap, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voir sur la carte")
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (label, color) = when (status) {
        "ACTIVE" -> "Actif" to MaterialTheme.colorScheme.primary
        "FULL" -> "Complet" to MaterialTheme.colorScheme.tertiary
        "COMPLETED" -> "Terminé" to MaterialTheme.colorScheme.outline
        "CANCELLED" -> "Annulé" to MaterialTheme.colorScheme.error
        else -> status to MaterialTheme.colorScheme.outline
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
    }
}