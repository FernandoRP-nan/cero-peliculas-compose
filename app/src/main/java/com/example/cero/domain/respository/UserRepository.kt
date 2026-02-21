package com.example.cero.domain.respository

import com.example.cero.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun observeUsers(): Flow<List<User>>

    suspend fun refreshUsers()
}