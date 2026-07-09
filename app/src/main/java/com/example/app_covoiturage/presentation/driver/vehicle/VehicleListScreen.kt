package com.example.app_covoiturage.presentation.driver.vehicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app_covoiturage.domain.model.Vehicle

@Composable
fun VehicleListScreen(
    onAddVehicle: () -> Unit,
    onEditVehicle: (String) -> Unit,
    viewModel: VehicleListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mes véhicules", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onAddVehicle) {
                Icon(Icons.Filled.Add, contentDescription = "Ajouter un véhicule")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.vehicles.isEmpty()) {
            Text("Aucun véhicule enregistré")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAddVehicle, modifier = Modifier.fillMaxWidth()) {
                Text("Ajouter un véhicule")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.vehicles) { vehicle ->
                    Card(
                        onClick = { onEditVehicle(vehicle.id!!) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.DirectionsCar, contentDescription = null, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("${vehicle.brand} ${vehicle.model}", fontWeight = FontWeight.Bold)
                                Text("Plaque : ${vehicle.plateNumber} • ${vehicle.seats} places")
                            }
                        }
                    }
                }
            }
        }
    }
}