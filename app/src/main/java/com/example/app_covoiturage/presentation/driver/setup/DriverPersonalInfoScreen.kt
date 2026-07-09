package com.example.app_covoiturage.presentation.driver.setup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverPersonalInfoScreen(
    onNext: () -> Unit,
    viewModel: DriverSetupViewModel = hiltViewModel()
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
        Text("Informations personnelles", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

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

        TextButton(onClick = { launcher.launch("image/*") }) {
            Text("Ajouter une photo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("Numéro de téléphone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Champ Date de naissance — lecture seule, ouvre un DatePicker au clic
        OutlinedTextField(
            value = state.dateOfBirth,
            onValueChange = {},
            readOnly = true,
            label = { Text("Date de naissance") },
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
                    }) {
                        Text("Confirmer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Annuler")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.savePersonalInfo(onNext) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Suivant")
        }
    }
}