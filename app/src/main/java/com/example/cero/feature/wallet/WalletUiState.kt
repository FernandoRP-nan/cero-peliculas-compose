package com.example.cero.feature.wallet

data class WalletUiState(
    val walletName: String = WalletLocalization.walletName(),
    val helperText: String = WalletLocalization.walletHelper(),
    val totalDebt: String = "$0",
    val monthlyCommitment: String = "$0/mes",
    val availableToSpend: String = "$0",
    val spendingMessage: String = "",
    val canSpendMore: Boolean = false,
    val currentScreen: WalletScreenDestination = WalletScreenDestination.Wallet,
    val isWalletOpen: Boolean = false,
    val isCardSelectorVisible: Boolean = false,
    val isAddCardVisible: Boolean = false,
    val isAddExpenseVisible: Boolean = false,
    val selectedCardId: String? = null,
    val actionMenuCardId: String? = null,
    val transitioningToExpenseCardId: String? = null,
    val quickEntryMode: AddExpenseEntryMode? = null,
    val pendingSelectorScrollToHidden: Boolean = false,
    val addCardMode: AddCardMode = AddCardMode.CREATE,
    val addCardForm: AddCardFormUiState = AddCardFormUiState(),
    val addExpenseForm: AddExpenseFormUiState = AddExpenseFormUiState(),
    val movementFilterMode: MovementFilterMode = MovementFilterMode.WEEK,
    val weekDayChips: List<MovementDayChipUiModel> = emptyList(),
    val selectedWeekDayKey: String? = null,
    val cards: List<WalletCardUiModel> = emptyList(),
    val expenseCard: WalletCardUiModel? = null,
    val expenseGroups: List<ExpenseDayGroupUiModel> = emptyList()
)

enum class WalletScreenDestination {
    Wallet,
    AddExpense
}

enum class AddCardMode {
    CREATE,
    EDIT
}

data class WalletCardUiModel(
    val id: String,
    val name: String,
    val bankName: String,
    val brand: String,
    val lastDigits: String,
    val creditLimitAmount: Double,
    val usedLimitAmount: Double,
    val limitUsageText: String,
    val availableLimitText: String,
    val availableLimitAmount: Double,
    val monthlyPaymentText: String,
    val monthlyPaymentAmount: Double,
    val installmentsText: String,
    val pendingMsiBalanceText: String,
    val pendingInstallmentsAmount: Double,
    val paidMsiText: String,
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

data class AddExpenseFormUiState(
    val mode: AddExpenseEntryMode = AddExpenseEntryMode.CHARGE,
    val paymentAllocationMode: PaymentAllocationMode = PaymentAllocationMode.GENERAL,
    val concept: String = "",
    val amount: String = "",
    val isMsi: Boolean = false,
    val installmentCount: String = "",
    val hasAttemptedSubmit: Boolean = false,
    val canSubmit: Boolean = false,
    val errorMessage: String? = null
)

data class ExpenseDayGroupUiModel(
    val dateLabel: String,
    val totalAmountText: String,
    val expenses: List<ExpenseItemUiModel>
)

data class ExpenseItemUiModel(
    val id: String,
    val concept: String,
    val amountText: String,
    val timeLabel: String,
    val supportingText: String? = null,
    val isPositive: Boolean = false
)

data class MovementDayChipUiModel(
    val key: String,
    val label: String,
    val dayNumber: String,
    val isSelected: Boolean
)

enum class MovementFilterMode {
    WEEK,
    MONTH
}

enum class AddExpenseEntryMode {
    CHARGE,
    PAYMENT
}

enum class PaymentAllocationMode {
    GENERAL,
    MSI_ONLY
}

enum class CardBrandOption {
    NONE,
    VISA,
    MASTERCARD
}
