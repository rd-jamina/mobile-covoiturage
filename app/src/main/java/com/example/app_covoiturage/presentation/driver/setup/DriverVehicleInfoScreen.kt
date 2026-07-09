package com.example.app_covoiturage.presentation.driver.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DriverVehicleInfoScreen(
    onNext: () -> Unit,
    viewModel: DriverSetupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val seatsValue = state.seats.toIntOrNull() ?: 1

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Informations véhicule", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.brand,
            onValueChange = viewModel::onBrandChange,
            label = { Text("Marque (ex: Toyota)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.model,
            onValueChange = viewModel::onModelChange,
            label = { Text("Modèle (ex: Corolla)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.plateNumber,
            onValueChange = viewModel::onPlateChange,
            label = { Text("Numéro de plaque") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Stepper pour le nombre de places — comme dans le prototype (- 3 +)
        Text("Nombre de places disponibles", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (seatsValue > 1) viewModel.onSeatsChange((seatsValue - 1).toString()) }
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "Diminuer")
            }
            Text(
                text = seatsValue.toString(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            IconButton(
                onClick = { viewModel.onSeatsChange((seatsValue + 1).toString()) }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Augmenter")
            }
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.saveVehicleInfo(onNext) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Suivant")
        }
    }
}