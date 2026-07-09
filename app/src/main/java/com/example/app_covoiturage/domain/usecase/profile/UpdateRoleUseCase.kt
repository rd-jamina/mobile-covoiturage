package com.example.app_covoiturage.domain.usecase.profile

import com.example.app_covoiturage.domain.model.Role
import com.example.app_covoiturage.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateRoleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(role: Role): Result<Unit> {
        return repository.updateRole(role)
    }
}