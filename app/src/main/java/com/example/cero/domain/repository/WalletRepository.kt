package com.example.cero.domain.repository

import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.CardExpense
import com.example.cero.domain.model.WalletSnapshot
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun observeWalletSnapshot(): Flow<WalletSnapshot>
    fun observeStoredCards(): Flow<List<CardAccount>>
    fun observeCardExpenses(): Flow<List<CardExpense>>
    suspend fun saveCard(card: CardAccount)
    suspend fun saveExpense(expense: CardExpense)
}
