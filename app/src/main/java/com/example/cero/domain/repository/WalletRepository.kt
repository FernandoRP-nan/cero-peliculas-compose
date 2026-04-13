package com.example.cero.domain.repository

import com.example.cero.domain.model.WalletSnapshot
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun observeWalletSnapshot(): Flow<WalletSnapshot>
}
