package com.example.app_covoiturage.presentation.passenger.setup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerPersonalInfoScreen(
    onNext: () -> Unit,
    viewModel: PassengerSetupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            viewModel.uploadPhoto(uri, context.contentResolver)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Information personnelle", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Complète ton profil pour commencer à réserver des trajets",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Photo de profil avec icône d'ajout/modification
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier.size(96.dp).clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null || state.photoUrl != null) {
                    AsyncImage(
                        model = selectedImageUri ?: state.photoUrl,
                        contentDescription = "Photo de profil",
                        modifier = Modifier.size(96.dp).clip(CircleShape)
                    )
                } else {
                    Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(96.dp))
                }
                if (state.isUploadingPhoto) CircularProgressIndicator()
            }
            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = "Ajouter une photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Numéro de téléphone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Section Genre
        Text("Genre", style = MaterialTheme.typography.labelLarge, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            GenderOption(
                label = "Homme",
                selected = state.gender == "MALE",
                onClick = { viewModel.onGenderChange("MALE") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            GenderOption(
                label = "Femme",
                selected = state.gender == "FEMALE",
                onClick = { viewModel.onGenderChange("FEMALE") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Date de naissance
        OutlinedTextField(
            value = formatDisplayDate(state.dateOfBirth),
            onValueChange = {},
            readOnly = true,
            label = { Text("Date de naissance") },
            placeholder = { Text("JJ/MM/AA") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = "Choisir une date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(millis)
                            viewModel.onDateOfBirthChange(formatted)
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

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.save(onNext) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Suivant")
        }
    }
}

@Composable
private fun GenderOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(onClick = onClick, modifier = modifier) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) { Text(label) }
    }
}

// Convertit "yyyy-MM-dd" (stocké) en "JJ/MM/AA" (affiché)
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