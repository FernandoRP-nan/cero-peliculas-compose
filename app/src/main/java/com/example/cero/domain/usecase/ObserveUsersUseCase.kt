package com.example.cero.domain.usecase


import com.example.cero.domain.respository.UserRepository

class ObserveUsersUseCase(
    private val repository: UserRepository
) {
    operator fun invoke() = repository.observeUsers()
}