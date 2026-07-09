package com.example.app_covoiturage.presentation.driver.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

data class DashboardAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun DriverDashboardScreen(
    onPublishTrip: () -> Unit,
    onReservationsReceived: () -> Unit,
    onTripHistory: () -> Unit,
    onNotifications: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DriverDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Bonjour, ${state.user?.fullName ?: "..."}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("Espace Chauffeur", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { viewModel.logout(onLogout) }) {
                Icon(Icons.Filled.Logout, contentDescription = "Déconnexion")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bouton principal — publier un trajet
        Button(
            onClick = onPublishTrip,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Publier un trajet")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Accès rapide", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        val actions = listOf(
            DashboardAction(Icons.Filled.Inbox, "Réservations reçues", onReservationsReceived),
            DashboardAction(Icons.Filled.History, "Historique des trajets", onTripHistory),
            DashboardAction(Icons.Filled.Notifications, "Notifications", onNotifications),
            DashboardAction(Icons.Filled.Person, "Mon profil", onProfile)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(actions) { action ->
                DashboardActionCard(action)
            }
        }
    }
}

@Composable
private fun DashboardActionCard(action: DashboardAction) {
    Card(
        onClick = action.onClick,
        modifier = Modifier.fillMaxWidth().height(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(action.icon, contentDescription = action.label, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(action.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}