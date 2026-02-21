package com.example.cero.data.repository


import com.example.cero.data.local.UserDao
import com.example.cero.data.mapper.toDomain
import com.example.cero.data.mapper.toEntity
import com.example.cero.data.remote.UserApi
import com.example.cero.domain.model.User
import com.example.cero.domain.respository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val api: UserApi,
    private val dao: UserDao
) : UserRepository {

    override fun observeUsers(): Flow<List<User>> =
        dao.observeUsers().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun refreshUsers() {
        val remote = api.getUsers()
        dao.insertAll(remote.map { it.toEntity() })
    }
}