package com.example.app_covoiturage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.app_covoiturage.presentation.navigation.AppNavHost
import com.example.app_covoiturage.ui.theme.App_covoiturageTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App_covoiturageTheme {
                AppNavHost()
            }
        }
    }
}