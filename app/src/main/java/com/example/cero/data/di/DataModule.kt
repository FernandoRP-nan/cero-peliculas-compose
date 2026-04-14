package com.example.cero.data.di

import com.example.cero.data.device.AndroidDeviceProfileRepository
import com.example.cero.data.repository.LocalWalletRepository
import com.example.cero.data.repository.UserRepositoryImpl
import com.example.cero.domain.repository.DeviceProfileRepository
import com.example.cero.domain.repository.WalletRepository
import com.example.cero.domain.respository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        impl: LocalWalletRepository
    ): WalletRepository

    @Binds
    @Singleton
    abstract fun bindDeviceProfileRepository(
        impl: AndroidDeviceProfileRepository
    ): DeviceProfileRepository
}
