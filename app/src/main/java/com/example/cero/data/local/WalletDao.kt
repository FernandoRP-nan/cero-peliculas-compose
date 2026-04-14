package com.example.cero.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query("SELECT * FROM cards ORDER BY displayName ASC")
    fun observeCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM card_expenses ORDER BY createdAt DESC")
    fun observeExpenses(): Flow<List<CardExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCard(card: CardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: CardExpenseEntity)
}
