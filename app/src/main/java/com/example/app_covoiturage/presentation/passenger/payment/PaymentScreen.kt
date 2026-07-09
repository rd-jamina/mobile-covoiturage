package com.example.app_covoiturage.presentation.passenger.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PaymentScreen(
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onPaymentSuccess()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Paiements", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total à payer", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${state.totalPrice.toInt()} Ar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Choisis ton moyen de paiement", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(12.dp))

        PaymentMethodOption(
            icon = Icons.Filled.PhoneAndroid,
            label = "Mobile Money",
            selected = state.selectedMethod == "MOBILE_MONEY",
            onClick = { viewModel.onMethodSelected("MOBILE_MONEY") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        PaymentMethodOption(
            icon = Icons.Filled.CreditCard,
            label = "Carte bancaire",
            selected = state.selectedMethod == "CARD",
            onClick = { viewModel.onMethodSelected("CARD") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        PaymentMethodOption(
            icon = Icons.Filled.AccountBalanceWallet,
            label = "Espèces (à la montée)",
            selected = state.selectedMethod == "CASH",
            onClick = { viewModel.onMethodSelected("CASH") }
        )

        state.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { viewModel.confirmPayment() },
            enabled = !state.isProcessing,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            if (state.isProcessing) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Payer ${state.totalPrice.toInt()} Ar")
        }
    }
}

@Composable
private fun PaymentMethodOption(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontWeight = FontWeight.Medium)
        }
    }
}