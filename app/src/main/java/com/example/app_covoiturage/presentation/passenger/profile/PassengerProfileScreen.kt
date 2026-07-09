package com.example.app_covoiturage.presentation.passenger.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
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

@Composable
fun PassengerProfileScreen(
    onEditPersonalInfo: () -> Unit,
    onPreferences: () -> Unit,
    onLogout: () -> Unit,
    viewModel: PassengerProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            viewModel.uploadPhoto(uri, context.contentResolver)
        }
    }

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
            // Photo de profil — cliquable pour ré-insérer / remplacer
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier.size(110.dp).clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null || state.photoUrl != null) {
                            AsyncImage(
                                model = selectedImageUri ?: state.photoUrl,
                                contentDescription = "Photo de profil",
                                modifier = Modifier.size(110.dp).clip(CircleShape)
                            )
                        } else {
                            Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(110.dp))
                        }
                        if (state.isUploadingPhoto) CircularProgressIndicator()
                    }
                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            Icons.Filled.PhotoCamera,
                            contentDescription = "Changer la photo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(state.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Informations", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Téléphone : ${state.phone.ifBlank { "Non renseigné" }}")
                    Text("Email : ${state.email}")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onEditPersonalInfo, modifier = Modifier.fillMaxWidth()) {
                        Text("Modifier mes informations")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Préférences de voyage", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onPreferences, modifier = Modifier.fillMaxWidth()) {
                        Text("Gérer mes préférences")
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