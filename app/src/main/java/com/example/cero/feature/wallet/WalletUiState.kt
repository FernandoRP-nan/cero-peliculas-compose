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
    val isCardSelectorVisible: Boolean = false,
    val isAddCardVisible: Boolean = false,
    val selectedCardId: String? = null,
    val addCardForm: AddCardFormUiState = AddCardFormUiState(),
    val cards: List<WalletCardUiModel> = emptyList()
)

data class WalletCardUiModel(
    val id: String,
    val name: String,
    val bankName: String,
    val brand: String,
    val lastDigits: String,
    val limitUsageText: String,
    val availableLimitText: String,
    val monthlyPaymentText: String,
    val installmentsText: String,
    val PaidMsiText: String,
    val paymentDayText: String,
    val closingDayText: String?,
    val accentStart: Long,
    val accentEnd: Long
)

data class AddCardFormUiState(
    val shortName: String = "",
    val bankName: String = "",
    val brand: CardBrandOption = CardBrandOption.NONE,
    val creditLimit: String = "",
    val availableLimit: String = "",
    val paymentDay: String = "",
    val hasClosingDay: Boolean = false,
    val closingDay: String = "",
    val hasAttemptedSubmit: Boolean = false,
    val canSubmit: Boolean = false,
    val errorMessage: String? = null
)

enum class CardBrandOption {
    NONE,
    VISA,
    MASTERCARD
}
