package com.example.cero.data.repository

import com.example.cero.data.local.WalletDao
import com.example.cero.data.mapper.toDomain
import com.example.cero.data.mapper.toEntity
import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.CardExpense
import com.example.cero.domain.model.CardExpenseType
import com.example.cero.domain.model.WalletSnapshot
import com.example.cero.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalWalletRepository @Inject constructor(
    private val walletDao: WalletDao
) : WalletRepository {

    override fun observeWalletSnapshot(): Flow<WalletSnapshot> {
        return combine(
            observeStoredCards(),
            observeCardExpenses()
        ) { storedCards, expenses ->
            val expensesByCard = expenses.groupBy { it.cardId }
            val adjustedCards = storedCards.map { card ->
                val cardMovements = expensesByCard[card.id].orEmpty()
                val totalCharges = cardMovements
                    .filter { it.entryType == CardExpenseType.CHARGE }
                    .sumOf(CardExpense::amount)
                val totalPayments = cardMovements
                    .filter { it.entryType == CardExpenseType.PAYMENT }
                    .sumOf(CardExpense::amount)
                val msiPurchases = cardMovements.filter { it.entryType == CardExpenseType.CHARGE && it.isMsi }
                val addedMsiMonthly = msiPurchases.sumOf { it.monthlyInstallmentAmount }
                val pendingMsiBalance = msiPurchases.sumOf(CardExpense::amount)
                val adjustedAvailable = (card.availableLimit - totalCharges + totalPayments)
                    .coerceIn(0.0, card.creditLimit)
                card.copy(
                    availableLimit = adjustedAvailable,
                    monthlyInstallmentPayment = card.monthlyInstallmentPayment + addedMsiMonthly,
                    pendingInstallments = card.pendingInstallments,
                    pendingMsiBalance = card.pendingMsiBalance + pendingMsiBalance
                )
            }
            val totalDebt = adjustedCards.sumOf { it.usedLimit }
            val monthlyCommitment = adjustedCards.sumOf(CardAccount::monthlyInstallmentPayment)
            val availableToSpend = adjustedCards.sumOf(CardAccount::availableLimit)

            WalletSnapshot(
                totalDebt = totalDebt,
                monthlyCommitment = monthlyCommitment,
                availableToSpend = availableToSpend,
                canSpendMore = availableToSpend > 0.0,
                cards = adjustedCards
            )
        }
    }

    override fun observeStoredCards(): Flow<List<CardAccount>> {
        return walletDao.observeCards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeCardExpenses(): Flow<List<CardExpense>> {
        return walletDao.observeExpenses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveCard(card: CardAccount) {
        walletDao.upsertCard(card.toEntity())
    }

    override suspend fun saveExpense(expense: CardExpense) {
        walletDao.insertExpense(expense.toEntity())
    }
}
