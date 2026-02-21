package com.example.cero.data.mapper

import com.example.cero.data.local.UserEntity
import com.example.cero.data.remote.UserDto
import com.example.cero.domain.model.User

fun UserDto.toEntity() = UserEntity(id, name, email)

fun UserEntity.toDomain() = User(id, name, email)