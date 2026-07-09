package com.example.app_covoiturage.domain.usecase.auth

import com.example.app_covoiturage.domain.model.Role
import com.example.app_covoiturage.domain.model.User
import com.example.app_covoiturage.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        fullName: String,
        role: Role?
    ): Result<User> {
        if (fullName.isBlank()) return Result.failure(Exception("Merci d'indiquer ton nom complet"))
        if (email.isBlank() || !email.contains("@")) return Result.failure(Exception("Adresse email invalide"))
        if (password.length < 6) return Result.failure(Exception("Le mot de passe doit contenir au moins 6 caractères"))
        if (role == null) return Result.failure(Exception("Merci de choisir Chauffeur ou Passager"))

        return repository.register(email, password, fullName, role)
    }
}