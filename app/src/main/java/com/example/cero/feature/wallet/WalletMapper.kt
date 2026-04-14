package com.example.cero.feature.wallet

import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.WalletSnapshot
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

internal fun WalletSnapshot.toUiState(
    isWalletOpen: Boolean,
    isCardSelectorVisible: Boolean,
    isAddCardVisible: Boolean,
    selectedCardId: String?,
    addCardForm: AddCardFormUiState,
    extraCards: List<CardAccount>
): WalletUiState {
    val spendingMessage = if (canSpendMore) {
        "Si puedes gastar mas, pero con margen cuidado"
    } else {
        "Por ahora mejor no cargues otra compra a MSI"
    }

    return WalletUiState(
        totalDebt = currencyFormatter.format(totalDebt),
        monthlyCommitment = "${currencyFormatter.format(monthlyCommitment)}/mes",
        availableToSpend = currencyFormatter.format(availableToSpend),
        spendingMessage = spendingMessage,
        canSpendMore = canSpendMore,
        isWalletOpen = isWalletOpen,
        isCardSelectorVisible = isCardSelectorVisible,
        isAddCardVisible = isAddCardVisible,
        selectedCardId = selectedCardId,
        addCardForm = addCardForm,
        cards = (cards + extraCards).mapIndexed { index, card ->
            card.toUiModel(index)
        }
    )
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
        monthlyPaymentText = currencyFormatter.format(monthlyInstallmentPayment),
        installmentsText = "$pendingInstallments MSI pendientes",
        accentStart = palette.first,
        accentEnd = palette.second,
        PaidMsiText = "$paidMsi MSI pagados",
        paymentDayText = "Pago el dia $paymentDay",
        closingDayText = closingDay?.let { "Corte el dia $it" }
    )
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
        monthlyPaymentText = currencyFormatter.format(0),
        installmentsText = "0 MSI pendientes",
        PaidMsiText = "0 MSI pagados",
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

internal fun AddCardFormUiState.toCardAccount(): CardAccount {
    val credit = creditLimit.toDouble()
    val available = availableLimit.toDouble()
    return CardAccount(
        id = "manual-${shortName.lowercase().replace(" ", "-")}-${(credit * 100).roundToInt()}",
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
