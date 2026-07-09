package com.example.app_covoiturage.presentation.onboarding

import androidx.annotation.DrawableRes
import com.example.app_covoiturage.R

data class OnboardingPage(
    @DrawableRes val image: Int,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingPage(
        image = R.drawable.ecran_d_acceuil,
        title = "Voyagez ensemble",
        description = "Partagez vos trajets et réduisez vos frais de transport"
    ),
    OnboardingPage(
        image = R.drawable.onboard2,
        title = "Trouvez votre trajet facilement",
        description = "Recherchez ou publiez un trajet en quelques secondes"
    )
)