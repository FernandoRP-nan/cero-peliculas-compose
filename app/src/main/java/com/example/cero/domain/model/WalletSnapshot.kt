package com.example.cero.domain.model

data class WalletSnapshot(
    val totalDebt: Double,
    val monthlyCommitment: Double,
    val availableToSpend: Double,
    val canSpendMore: Boolean,
    val cards: List<CardAccount>
)
