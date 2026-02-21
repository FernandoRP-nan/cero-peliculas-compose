package com.example.cero.domain.usecase

import com.example.cero.domain.respository.UserRepository

class RefreshUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.refreshUsers()
}