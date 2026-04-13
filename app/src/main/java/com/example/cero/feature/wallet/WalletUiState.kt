package com.example.cero.feature.wallet

data class WalletUiState(
    val walletName: String = "Tu cartera",
    val helperText: String = "Toca para abrir y revisar tus tarjetas",
    val totalDebt: String = "$0",
    val monthlyCommitment: String = "$0/mes",
    val availableToSpend: String = "$0",
    val spendingMessage: String = "",
    val canSpendMore: Boolean = false,
    val isWalletOpen: Boolean = false,
    val cards: List<WalletCardUiModel> = emptyList()
)

data class WalletCardUiModel(
    val id: String,
    val name: String,
    val brand: String,
    val lastDigits: String,
    val limitUsageText: String,
    val monthlyPaymentText: String,
    val installmentsText: String,
    val accentStart: Long,
    val accentEnd: Long
)
