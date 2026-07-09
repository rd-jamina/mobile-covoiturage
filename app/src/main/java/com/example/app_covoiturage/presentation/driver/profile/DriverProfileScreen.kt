package com.example.app_covoiturage.presentation.driver.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DriverProfileScreen(
    onEditPersonalInfo: () -> Unit,
    onEditVehicle: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DriverProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mon profil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            IconButton(onClick = { viewModel.logout(onLogout) }) {
                Icon(Icons.Filled.Logout, contentDescription = "Déconnexion")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Carte infos personnelles
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Person, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Informations personnelles", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Nom : ${state.fullName}")
                    Text("Téléphone : ${state.phone.ifBlank { "Non renseigné" }}")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onEditPersonalInfo, modifier = Modifier.fillMaxWidth()) {
                        Text("Modifier")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Carte véhicule
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DirectionsCar, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Véhicule", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.vehicle != null) {
                        Text("${state.vehicle!!.brand} ${state.vehicle!!.model}")
                        Text("Plaque : ${state.vehicle!!.plate_number}")
                        Text("Places : ${state.vehicle!!.seats}")
                    } else {
                        Text("Aucun véhicule enregistré")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onEditVehicle, modifier = Modifier.fillMaxWidth()) {
                        Text("Modifier")
                    }
                }
            }

            state.error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}