package com.example.app_covoiturage.presentation.passenger.trip.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTripScreen(
    onBack: () -> Unit,
    onSearch: (origin: String, destination: String, date: String) -> Unit,
    viewModel: SearchTripViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Rechercher un trajet", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.origin,
            onValueChange = viewModel::onOriginChange,
            label = { Text("Lieu de départ") },
            placeholder = { Text("Ex: Antananarivo") },
            leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.destination,
            onValueChange = viewModel::onDestinationChange,
            label = { Text("Lieu d'arrivée") },
            placeholder = { Text("Ex: Antsirabe") },
            leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = formatDisplayDate(state.date),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            placeholder = { Text("JJ/MM/AA") },
            leadingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .then(Modifier),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = "Choisir une date")
                }
            }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(millis)
                            viewModel.onDateChange(formatted)
                        }
                        showDatePicker = false
                    }) { Text("Confirmer") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (viewModel.validate()) {
                    onSearch(state.origin, state.destination, state.date)
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Rechercher")
        }
    }
}

private fun formatDisplayDate(isoDate: String): String {
    if (isoDate.isBlank()) return ""
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        output.format(input.parse(isoDate)!!)
    } catch (e: Exception) {
        isoDate
    }
}