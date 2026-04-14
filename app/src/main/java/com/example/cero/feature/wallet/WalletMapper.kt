package com.example.cero.feature.wallet

import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.CardExpense
import com.example.cero.domain.model.CardExpenseType
import com.example.cero.domain.model.WalletSnapshot
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
private val expenseDateFormatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "MX"))
private val expenseTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale("es", "MX"))
private val weekDayFormatter = DateTimeFormatter.ofPattern("EEE", Locale("es", "MX"))

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
    movementFilterMode: MovementFilterMode,
    selectedWeekDayKey: String?,
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
    val allCardMovements = expensesByCard[selectedCardId].orEmpty()
    val weekDayChips = buildWeekDayChips(
        movements = allCardMovements,
        selectedKey = selectedWeekDayKey
    )
    val effectiveWeekDayKey = selectedWeekDayKey ?: weekDayChips.firstOrNull()?.key
    val filteredMovements = when (movementFilterMode) {
        MovementFilterMode.WEEK -> allCardMovements.filter {
            it.createdAt.toLocalDate().toString() == effectiveWeekDayKey
        }
        MovementFilterMode.MONTH -> allCardMovements
    }

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
        movementFilterMode = movementFilterMode,
        weekDayChips = weekDayChips.map { chip ->
            chip.copy(isSelected = chip.key == effectiveWeekDayKey)
        },
        selectedWeekDayKey = effectiveWeekDayKey,
        cards = cardsUi,
        expenseCard = selectedExpenseCard,
        expenseGroups = filteredMovements.toExpenseGroups()
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
        creditLimitAmount = creditLimit,
        usedLimitAmount = usedLimit,
        limitUsageText = "${currencyFormatter.format(usedLimit)} de ${currencyFormatter.format(creditLimit)}",
        availableLimitText = currencyFormatter.format(availableLimit),
        availableLimitAmount = availableLimit,
        monthlyPaymentText = currencyFormatter.format(monthlyInstallmentPayment),
        monthlyPaymentAmount = monthlyInstallmentPayment,
        installmentsText = currencyFormatter.format(pendingMsiBalance),
        pendingInstallmentsAmount = pendingMsiBalance,
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
                        concept = expense.displayConcept(),
                        amountText = buildAmountLabel(expense),
                        timeLabel = expense.createdAt.format(expenseTimeFormatter),
                        supportingText = if (expense.isMsi && expense.installmentCount != null) {
                            "${expense.installmentCount} MSI · ${currencyFormatter.format(expense.monthlyInstallmentAmount)}/mes"
                        } else if (expense.entryType == CardExpenseType.PAYMENT) {
                            "Pago registrado manualmente"
                        } else {
                            null
                        },
                        isPositive = expense.entryType == CardExpenseType.PAYMENT
                    )
                }
            )
        }
}

private fun buildAmountLabel(expense: CardExpense): String {
    val amount = currencyFormatter.format(expense.amount)
    return if (expense.entryType == CardExpenseType.PAYMENT) "+$amount" else amount
}

private fun buildWeekDayChips(
    movements: List<CardExpense>,
    selectedKey: String?
): List<MovementDayChipUiModel> {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val availableDays = if (movements.isEmpty()) {
        (0..6).map { startOfWeek.plusDays(it.toLong()) }
    } else {
        (0..6).map { startOfWeek.plusDays(it.toLong()) }
            .filter { day -> movements.any { it.createdAt.toLocalDate() == day } }
            .ifEmpty { listOf(today) }
    }
    val effectiveKey = selectedKey ?: availableDays.first().toString()

    return availableDays.map { day ->
        MovementDayChipUiModel(
            key = day.toString(),
            label = day.format(weekDayFormatter).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale("es", "MX")) else it.toString()
            },
            dayNumber = day.dayOfMonth.toString(),
            isSelected = day.toString() == effectiveKey
        )
    }
}

private fun CardExpense.displayConcept(): String {
    return when {
        entryType == CardExpenseType.PAYMENT -> "Pago: $concept"
        isMsi -> "$concept (MSI)"
        else -> concept
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
        creditLimitAmount = credit,
        usedLimitAmount = (credit - available).coerceAtLeast(0.0),
        limitUsageText = "${currencyFormatter.format((credit - available).coerceAtLeast(0.0))} de ${currencyFormatter.format(credit)}",
        availableLimitText = currencyFormatter.format(available),
        availableLimitAmount = available,
        monthlyPaymentText = currencyFormatter.format(0),
        monthlyPaymentAmount = 0.0,
        installmentsText = currencyFormatter.format(0),
        pendingInstallmentsAmount = 0.0,
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
        pendingMsiBalance = 0.0,
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
