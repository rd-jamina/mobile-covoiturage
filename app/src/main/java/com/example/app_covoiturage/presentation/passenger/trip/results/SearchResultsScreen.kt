package com.example.app_covoiturage.presentation.passenger.trip.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app_covoiturage.domain.model.Trip

@Composable
fun SearchResultsScreen(
    onBack: () -> Unit,
    onTripClick: (String) -> Unit,
    viewModel: SearchResultsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Liste des résultats", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
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
            state.trips.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Aucun trajet trouvé pour ces critères")
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.trips) { trip ->
                        TripResultCard(trip, onClick = { onTripClick(trip.id ?: "") })
                    }
                }
            }
        }
    }
}

@Composable
private fun TripResultCard(trip: Trip, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${trip.origin} → ${trip.destination}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Départ : ${trip.departureTime}")
            Text("Places disponibles : ${trip.availableSeats}")
            Text("${trip.price.toInt()} Ar / place", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text("Info détaillé")
            }
        }
    }
}