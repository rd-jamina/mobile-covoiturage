package com.example.app_covoiturage.domain.usecase.auth

import com.example.app_covoiturage.domain.model.User
import com.example.app_covoiturage.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email et mot de passe requis"))
        }
        return repository.login(email, password)
    }
}