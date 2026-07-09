package com.example.app_covoiturage.presentation.driver.trip.publish

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PublishTripScreen(
    onPublished: () -> Unit,
    viewModel: PublishTripViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onPublished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Publier un trajet", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.origin,
            onValueChange = viewModel::onOriginChange,
            label = { Text("Point de départ") },
            placeholder = { Text("Ex: Antananarivo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.destination,
            onValueChange = viewModel::onDestinationChange,
            label = { Text("Destination") },
            placeholder = { Text("Ex: Antsirabe") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.departureTime,
            onValueChange = viewModel::onDepartureTimeChange,
            label = { Text("Date et heure de départ") },
            placeholder = { Text("2026-07-15 08:30") },
            supportingText = { Text("Format: AAAA-MM-JJ HH:MM") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.seats,
            onValueChange = viewModel::onSeatsChange,
            label = { Text("Places disponibles") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.price,
            onValueChange = viewModel::onPriceChange,
            label = { Text("Prix par place (Ar)") },
            modifier = Modifier.fillMaxWidth()
        )

        state.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.publish() },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Publier")
            }
        }
    }
}