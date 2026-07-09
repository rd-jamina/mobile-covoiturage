package com.example.app_covoiturage.presentation.passenger.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PreferencesScreen(
    onBack: () -> Unit,
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Préférences de voyage", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PreferenceSwitchRow(
                label = "Musique pendant le trajet",
                checked = state.musicAllowed,
                onCheckedChange = viewModel::onMusicChange
            )
            HorizontalDivider()

            PreferenceSwitchRow(
                label = "Fumeur toléré",
                checked = state.smokingAllowed,
                onCheckedChange = viewModel::onSmokingChange
            )
            HorizontalDivider()

            PreferenceSwitchRow(
                label = "Animaux de compagnie acceptés",
                checked = state.petsAllowed,
                onCheckedChange = viewModel::onPetsChange
            )
            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))
            Text("Préférence de discussion", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))

            ChatPreferenceOption("Silencieux", "SILENT", state.chatPreference, viewModel::onChatPreferenceChange)
            ChatPreferenceOption("Modéré", "MODERATE", state.chatPreference, viewModel::onChatPreferenceChange)
            ChatPreferenceOption("Bavard", "CHATTY", state.chatPreference, viewModel::onChatPreferenceChange)

            state.error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.save() },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Enregistrer")
            }
        }
    }
}

@Composable
private fun PreferenceSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ChatPreferenceOption(
    label: String,
    value: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected == value, onClick = { onSelect(value) })
        Text(label)
    }
}