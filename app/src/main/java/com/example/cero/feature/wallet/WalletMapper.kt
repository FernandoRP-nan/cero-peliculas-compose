package com.example.cero.feature.wallet

import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.CardExpense
import com.example.cero.domain.model.WalletSnapshot
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
private val expenseDateFormatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "MX"))
private val expenseTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale("es", "MX"))

internal fun WalletSnapshot.toUiState(
    currentScreen: WalletScreenDestination,
    isWalletOpen: Boolean,
    isCardSelectorVisible: Boolean,
    isAddCardVisible: Boolean,
    isAddExpenseVisible: Boolean,
    selectedCardId: String?,
    actionMenuCardId: String?,
    transitioningToExpenseCardId: String?,
    pendingSelectorScrollToHidden: Boolean,
    addCardMode: AddCardMode,
    addCardForm: AddCardFormUiState,
    addExpenseForm: AddExpenseFormUiState,
    extraCards: List<CardAccount>,
    editedCards: Map<String, CardAccount>,
    expensesByCard: Map<String, List<CardExpense>>
): WalletUiState {
    val mergedCards = mergeCards(
        baseCards = cards,
        extraCards = extraCards,
        editedCards = editedCards
    )
    val spendingMessage = if (canSpendMore) {
        "Si puedes gastar mas, pero con margen cuidado"
    } else {
        "Por ahora mejor no cargues otra compra a MSI"
    }
    val cardsUi = mergedCards.mapIndexed { index, card -> card.toUiModel(index) }
    val selectedExpenseCard = cardsUi.firstOrNull { it.id == selectedCardId }

    return WalletUiState(
        totalDebt = currencyFormatter.format(totalDebt),
        monthlyCommitment = "${currencyFormatter.format(monthlyCommitment)}/mes",
        availableToSpend = currencyFormatter.format(availableToSpend),
        spendingMessage = spendingMessage,
        canSpendMore = canSpendMore,
        currentScreen = currentScreen,
        isWalletOpen = isWalletOpen,
        isCardSelectorVisible = isCardSelectorVisible,
        isAddCardVisible = isAddCardVisible,
        isAddExpenseVisible = isAddExpenseVisible,
        selectedCardId = selectedCardId,
        actionMenuCardId = actionMenuCardId,
        transitioningToExpenseCardId = transitioningToExpenseCardId,
        pendingSelectorScrollToHidden = pendingSelectorScrollToHidden,
        addCardMode = addCardMode,
        addCardForm = addCardForm,
        addExpenseForm = addExpenseForm,
        cards = cardsUi,
        expenseCard = selectedExpenseCard,
        expenseGroups = expensesByCard[selectedCardId].orEmpty().toExpenseGroups()
    )
}

private fun mergeCards(
    baseCards: List<CardAccount>,
    extraCards: List<CardAccount>,
    editedCards: Map<String, CardAccount>
): List<CardAccount> {
    return (baseCards + extraCards).map { original ->
        editedCards[original.id] ?: original
    }
}

private fun CardAccount.toUiModel(index: Int): WalletCardUiModel {
    val palette = when (index % 3) {
        0 -> 0xFF355C7D to 0xFF6C5B7B
        1 -> 0xFF0F766E to 0xFF115E59
        else -> 0xFF9A3412 to 0xFFEA580C
    }

    return WalletCardUiModel(
        id = id,
        name = displayName,
        bankName = bankName.orEmpty(),
        brand = brand.orEmpty(),
        lastDigits = lastDigits,
        limitUsageText = "${currencyFormatter.format(usedLimit)} de ${currencyFormatter.format(creditLimit)}",
        availableLimitText = currencyFormatter.format(availableLimit),
        availableLimitAmount = availableLimit,
        monthlyPaymentText = currencyFormatter.format(monthlyInstallmentPayment),
        monthlyPaymentAmount = monthlyInstallmentPayment,
        installmentsText = "$pendingInstallments MSI pendientes",
        accentStart = palette.first,
        accentEnd = palette.second,
        paidMsiText = "$paidMsi MSI pagados",
        paymentDayText = "Pago el dia $paymentDay",
        closingDayText = closingDay?.let { "Corte el dia $it" }
    )
}

private fun List<CardExpense>.toExpenseGroups(): List<ExpenseDayGroupUiModel> {
    return sortedByDescending { it.createdAt }
        .groupBy { it.createdAt.toLocalDate() }
        .map { (date, expenses) ->
            ExpenseDayGroupUiModel(
                dateLabel = date.format(expenseDateFormatter)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "MX")) else it.toString() },
                totalAmountText = currencyFormatter.format(expenses.sumOf(CardExpense::amount)),
                expenses = expenses.sortedByDescending { it.createdAt }.map { expense ->
                    ExpenseItemUiModel(
                        id = expense.id,
                        concept = expense.concept,
                        amountText = currencyFormatter.format(expense.amount),
                        timeLabel = expense.createdAt.format(expenseTimeFormatter),
                        supportingText = if (expense.isMsi && expense.installmentCount != null) {
                            "${expense.installmentCount} MSI · ${currencyFormatter.format(expense.monthlyInstallmentAmount)}/mes"
                        } else {
                            null
                        }
                    )
                }
            )
        }
}

internal fun AddCardFormUiState.toPreviewCardUiModel(): WalletCardUiModel {
    val credit = creditLimit.toDoubleOrNull() ?: 0.0
    val available = availableLimit.toDoubleOrNull() ?: 0.0
    return WalletCardUiModel(
        id = "preview",
        name = shortName.ifBlank { "Nueva tarjeta" },
        bankName = bankName,
        brand = brand.label,
        lastDigits = "0000",
        limitUsageText = "${currencyFormatter.format((credit - available).coerceAtLeast(0.0))} de ${currencyFormatter.format(credit)}",
        availableLimitText = currencyFormatter.format(available),
        availableLimitAmount = available,
        monthlyPaymentText = currencyFormatter.format(0),
        monthlyPaymentAmount = 0.0,
        installmentsText = "0 MSI pendientes",
        paidMsiText = "0 MSI pagados",
        paymentDayText = paymentDay.toIntOrNull()?.let { "Pago el dia $it" } ?: "Agrega dia de pago",
        closingDayText = if (hasClosingDay) {
            closingDay.toIntOrNull()?.let { "Corte el dia $it" } ?: "Agrega dia de corte"
        } else {
            "Sin fecha de corte"
        },
        accentStart = 0xFF7C4D32,
        accentEnd = 0xFFBF8B63
    )
}

internal fun CardAccount.toAddCardForm(): AddCardFormUiState {
    return AddCardFormUiState(
        shortName = displayName,
        bankName = bankName.orEmpty(),
        brand = brand.toCardBrandOption(),
        creditLimit = normalizeNumber(creditLimit),
        availableLimit = normalizeNumber(availableLimit),
        paymentDay = paymentDay.toString(),
        hasClosingDay = closingDay != null,
        closingDay = closingDay?.toString().orEmpty(),
        canSubmit = true
    )
}

internal fun AddCardFormUiState.toCardAccount(existingCardId: String? = null): CardAccount {
    val credit = creditLimit.toDouble()
    val available = availableLimit.toDouble()
    return CardAccount(
        id = existingCardId ?: "manual-${shortName.lowercase().replace(" ", "-")}-${(credit * 100).roundToInt()}",
        displayName = shortName.trim(),
        bankName = bankName.takeIf { it.isNotBlank() }?.trim(),
        brand = brand.label.takeIf { it.isNotBlank() },
        lastDigits = "",
        creditLimit = credit,
        availableLimit = available,
        paymentDay = paymentDay.toInt(),
        closingDay = if (hasClosingDay) closingDay.toInt() else null,
        monthlyInstallmentPayment = 0.0,
        pendingInstallments = 0,
        paidMsi = 0
    )
}

internal val CardBrandOption.label: String
    get() = when (this) {
        CardBrandOption.NONE -> ""
        CardBrandOption.VISA -> "Visa"
        CardBrandOption.MASTERCARD -> "Mastercard"
    }

private fun String?.toCardBrandOption(): CardBrandOption {
    return when (this?.lowercase()) {
        "visa" -> CardBrandOption.VISA
        "mastercard" -> CardBrandOption.MASTERCARD
        else -> CardBrandOption.NONE
    }
}

private fun normalizeNumber(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
}
