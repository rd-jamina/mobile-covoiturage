package com.example.app_covoiturage.domain.repository

import com.example.app_covoiturage.domain.model.Role
import com.example.app_covoiturage.domain.model.User

interface AuthRepository {
    suspend fun register(email: String, password: String, fullName: String, role: Role): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
    suspend fun updateRole(role: Role): Result<Unit>
}