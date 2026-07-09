package com.example.app_covoiturage.presentation.driver.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DriverDocumentScreen(
    onFinished: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Vérification des documents", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ajoute une photo de ton permis de conduire et de ta carte grise. (Upload à implémenter avec Supabase Storage)")

        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = { /* TODO: ouvrir la galerie / caméra */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Choisir une photo — Permis")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Choisir une photo — Carte grise")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onFinished, modifier = Modifier.fillMaxWidth()) {
            Text("Soumettre")
        }
    }
}