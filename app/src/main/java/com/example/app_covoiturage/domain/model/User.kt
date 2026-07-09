package com.example.app_covoiturage.domain.model

data class User(
    val id: String,
    val fullName: String?,
    val phone: String?,
    val photoUrl: String?,
    val activeRole: Role? = null   // ← nullable maintenant
)

enum class Role {
    DRIVER, PASSENGER
}