package com.example.app_covoiturage.presentation.driver.vehicle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VehicleFormScreen(
    vehicleId: String?,
    onSaved: () -> Unit,
    viewModel: VehicleFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(vehicleId) {
        if (vehicleId != null) viewModel.loadForEdit(vehicleId)
    }
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSaved()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            if (vehicleId == null) "Ajouter un véhicule" else "Modifier le véhicule",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            OutlinedTextField(state.brand, viewModel::onBrandChange, label = { Text("Marque") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(state.model, viewModel::onModelChange, label = { Text("Modèle") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(state.plateNumber, viewModel::onPlateChange, label = { Text("Numéro de plaque") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(state.seats, viewModel::onSeatsChange, label = { Text("Nombre de places") }, modifier = Modifier.fillMaxWidth())

            state.error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { viewModel.save() }, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth()) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Enregistrer")
            }
        }
    }
}